package se.sundsvall.sokigo.arendeexport;

import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.sokigo.CaseUtil;
import se.sundsvall.sokigo.fb.FastighetDto;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.ArchiveMetadata;
import se.sundsvall.vo.Attachment;
import se.sundsvall.vo.AttachmentCategory;
import se.sundsvall.vo.SystemType;
import se.tekis.arende.*;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.BatchFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ByggrMapper {

    @Inject
    Logger log;

    @Inject
    CaseUtil caseUtil;

    @Inject
    ArendeExportIntegrationService arendeExportIntegrationService;

    public BatchFilter getBatchFilter(LocalDateTime start, LocalDateTime end) {

        BatchFilter filter = new BatchFilter();
        filter.setLowerExclusiveBound(start);
        filter.setUpperInclusiveBound(end);

        return filter;
    }

    public void setAttachmentFields(List<String> secrecyDocumentList, se.tekis.arende.Arende arende, Handling handling, Dokument doc, Attachment attachment) throws ApplicationException {
        setAttachmentCategory(handling, attachment);

        attachment.setExtension("." + doc.getFil().getFilAndelse().toLowerCase());
        attachment.setMimeType(null);
        attachment.setName(doc.getNamn());
        attachment.setNote(doc.getBeskrivning());
        attachment.setFile(caseUtil.byteArrayToBase64(doc.getFil().getFilBuffer()));

        setAttachmentArchiveMetadata(secrecyDocumentList, arende, doc, attachment);
    }

    public void setAttachmentArchiveMetadata(List<String> secrecyDocumentList, se.tekis.arende.Arende arende, Dokument doc, Attachment attachment) throws ApplicationException {
        ArchiveMetadata archiveMetadata = new ArchiveMetadata();

        if (secrecyDocumentList.contains(doc.getDokId())) {
            archiveMetadata.setSecrecy(true);
        }

        archiveMetadata.setSystem(SystemType.BYGGR);
        archiveMetadata.setCaseId(arende.getDnr());
        archiveMetadata.setDocumentId(doc.getDokId());
        // All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL,
        // which means they get the archiveClassification D,
        // which means that they are not public in the archive.
        archiveMetadata.setArchiveClassification(attachment.getCategory().getArchiveClassification());
        archiveMetadata.setCaseTitle(arende.getBeskrivning());
        archiveMetadata.setDocumentCreatedAt(doc.getSkapadDatum());

        archiveMetadata.setCaseCreatedAt(arende.getRegistreradDatum());
        archiveMetadata.setCaseEndedAt(arende.getSlutDatum());
        setCaseEndDecision(arende.getHandelseLista().getHandelse(), archiveMetadata);

        if (arende.getObjektLista() != null
                && arende.getObjektLista().getAbstractArendeObjekt() != null) {
            setPropertyFields(arende.getObjektLista().getAbstractArendeObjekt(), archiveMetadata);
        }

        attachment.setArchiveMetadata(archiveMetadata);
    }

    public void setCaseEndDecision(List<Handelse> handelseList, ArchiveMetadata archiveMetadata) {
        List<Handelse> finalDecisionList = handelseList
                .stream().filter(h -> Constants.BYGGR_HANDELSESLAG_SLUTBESKED.equals(h.getHandelseslag()))
                .sorted(Comparator.comparing(Handelse::getStartDatum).reversed())
                .collect(Collectors.toList());

        // Use the latest
        if (!finalDecisionList.isEmpty()) {
            archiveMetadata.setCaseEndDecisionAt(finalDecisionList.get(0).getStartDatum());
        }
    }

    public void setPropertyFields(List<AbstractArendeObjekt> abstractArendeObjektList, ArchiveMetadata archiveMetadata) throws ApplicationException {
        for (AbstractArendeObjekt abstractArendeObjekt : abstractArendeObjektList) {
            ArendeFastighet arendeFastighet;

            try {
                arendeFastighet = (ArendeFastighet) abstractArendeObjekt;
            } catch (ClassCastException e) {
                log.info("Could not cast AbstractArendeObjekt to ArendeFastighet");
                continue;
            }

            if (arendeFastighet != null
                    && arendeFastighet.isArHuvudObjekt()) {
                FastighetDto fastighetDto = caseUtil.getPropertyInfoByFnr(arendeFastighet.getFastighet().getFnr());
                archiveMetadata.setPropertyDesignation(fastighetDto.getKommun() + " " + fastighetDto.getBeteckning());
                archiveMetadata.setRegion(fastighetDto.getTrakt());
                archiveMetadata.setRegisterUnit(fastighetDto.getUuid());
            }
        }
    }

    public void setAttachmentCategory(Handling handling, Attachment attachment) {
        try {
            attachment.setCategory(AttachmentCategory.valueOf(handling.getTyp()));
        } catch (IllegalArgumentException e) {
            // All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL,
            // which means they get the archiveClassification D,
            // which means that they are not public in the archive.
            attachment.setCategory(AttachmentCategory.BIL);
        }
    }

    public List<String> getSecrecyDocumentsList(List<Handelse> handelseList) {
        List<String> secrecyDocumentList = new ArrayList<>();
        for (Handelse handelse : handelseList) {
            if (handelse.isSekretess()) {
                for (Handling handling : handelse.getHandlingLista().getHandling()) {
                    if (handling.getDokument() != null) {
                        secrecyDocumentList.add(handling.getDokument().getDokId());
                    }
                }
            }
        }
        return secrecyDocumentList;
    }

    /**
     * Sets setLowerExclusiveBound to the returned batchEnd if it is not equal or before the latest batch. If it is, we add 1 hour.
     * After this, we run the batch again.
     */
    public void setLowerExclusiveBoundWithReturnedValue(BatchFilter filter, ArendeBatch arendeBatch) {
        if (arendeBatch != null) {
            log.info("Last ArendeBatch start: " + arendeBatch.getBatchStart() + " end: " + arendeBatch.getBatchEnd());
            if (arendeBatch.getBatchEnd() == null
                    || arendeBatch.getBatchEnd().isEqual(filter.getLowerExclusiveBound())
                    || arendeBatch.getBatchEnd().isBefore(filter.getLowerExclusiveBound())
                    || Duration.between(arendeBatch.getBatchStart(), arendeBatch.getBatchEnd()).toMinutes() <= 60) {

                LocalDateTime plusOneHour = filter.getLowerExclusiveBound().plusHours(1);
                filter.setLowerExclusiveBound(plusOneHour.isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : plusOneHour);

            } else {
                filter.setLowerExclusiveBound(arendeBatch.getBatchEnd().isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : arendeBatch.getBatchEnd());
            }
        }
    }


    public LocalDateTime getEnd(LocalDate searchEnd) {

        if (searchEnd.isBefore(LocalDate.now())) {
            return searchEnd.atTime(23, 59, 59);
        }

        return LocalDateTime.now();
    }


}
