package se.sundsvall.sokigo.arendeexport;

import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.sokigo.CaseUtil;
import se.sundsvall.sokigo.fb.FastighetDto;
import se.sundsvall.vo.ArchiveMetadata;
import se.sundsvall.vo.Attachment;
import se.sundsvall.vo.AttachmentCategory;
import se.sundsvall.vo.SystemType;
import se.sundsvall.util.Constants;
import se.tekis.arende.*;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.BatchFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
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

    private BatchFilter getBatchFilter(LocalDateTime start, LocalDateTime end) {

        BatchFilter filter = new BatchFilter();
        filter.setLowerExclusiveBound(start);
        filter.setUpperInclusiveBound(end);

        return filter;
    }

    public List<Attachment> getArchiveableAttachments(String searchStart, String searchEnd) throws ApplicationException {

        // Used for logging only
        List<se.tekis.arende.Arende> foundCases = new ArrayList<>();
        List<se.tekis.arende.Arende> foundClosedCases = new ArrayList<>();
        List<Dokument> foundDocuments = new ArrayList<>();

        LocalDateTime start = getStart(searchStart);
        LocalDateTime end = getEnd(searchEnd);
        BatchFilter batchFilter = getBatchFilter(start, end);

        ArendeBatch arendeBatch = null;
        // The attachmentList that will be returned
        List<Attachment> attachmentList = new ArrayList<>();

        do {
            setLowerExclusiveBoundWithReturnedValue(batchFilter, arendeBatch);

            log.info("Runs batch for start-date: " + batchFilter.getLowerExclusiveBound() + " and end-date: " + batchFilter.getUpperInclusiveBound());

            // Get arenden from Byggr
            arendeBatch = arendeExportIntegrationService.getUpdatedArenden(batchFilter);
            if (arendeBatch == null) {
                throw new ApplicationException("The response from arendeExportIntegrationService.getUpdatedArenden(batchFilter) was null. This shouldn't happen.");
            }

            foundCases.addAll(arendeBatch.getArenden().getArende());

            List<String> secrecyDocumentList;

            List<se.tekis.arende.Arende> closedCaseList = arendeBatch.getArenden().getArende().stream()
                    .filter(arende -> arende.getStatus().equals(Constants.BYGGR_STATUS_AVSLUTAT))
                    .collect(Collectors.toList());

            for (se.tekis.arende.Arende closedCase : closedCaseList) {
                foundClosedCases.add(closedCase);
                secrecyDocumentList = getSecrecyDocumentsList(closedCase.getHandelseLista().getHandelse());

                List<HandelseHandling> handelseHandlingList = closedCase.getHandelseLista().getHandelse().stream()
                        .filter(handelse -> Constants.BYGGR_HANDELSETYP_ARKIV.equals(handelse.getHandelsetyp()))
                        .flatMap(handelse -> handelse.getHandlingLista().getHandling().stream())
                        .collect(Collectors.toList());

                for (Handling handling : handelseHandlingList) {
                    if (handling.getDokument() != null) {

                        // Get documents from byggr
                        List<Dokument> dokumentList = arendeExportIntegrationService.getDocument(handling.getDokument().getDokId());

                        if (dokumentList == null) {
                            throw new ApplicationException("The response from arendeExportIntegrationService.getDocument(" + handling.getDokument().getDokId() + ") was null. Something went wrong...");
                        }

                        for (Dokument doc : dokumentList) {
                            foundDocuments.add(doc);
                            log.info("Document-Count: " + foundDocuments.size() + ". Found a document that should be archived - Case-ID: " + closedCase.getDnr() + " Document-ID: " + doc.getDokId() + " Document-name: " + doc.getNamn() + " Handling-ID: " + handling.getHandlingId() + " Handlingstyp: " + handling.getTyp());

                            Attachment attachment = new Attachment();
                            setAttachmentFields(secrecyDocumentList, closedCase, handling, doc, attachment);
                            attachmentList.add(attachment);
                        }
                    }
                }
            }
        } while (batchFilter.getLowerExclusiveBound().isBefore(end));

        log.info("\nTotalt antal arenden: " + foundCases.size()
                + "\nAntal avslutade arenden: " + foundClosedCases.size()
                + "\nAntal dokument som ska arkiveras: " + foundDocuments.size());

        return attachmentList;
    }

    private void setAttachmentFields(List<String> secrecyDocumentList, se.tekis.arende.Arende arende, Handling handling, Dokument doc, Attachment attachment) throws ApplicationException {
        setAttachmentCategory(handling, attachment);

        attachment.setExtension("." + doc.getFil().getFilAndelse().toLowerCase());
        attachment.setMimeType(null);
        attachment.setName(doc.getNamn());
        attachment.setNote(doc.getBeskrivning());
        attachment.setFile(caseUtil.byteArrayToBase64(doc.getFil().getFilBuffer()));

        setAttachmentArchiveMetadata(secrecyDocumentList, arende, doc, attachment);
    }

    private void setAttachmentArchiveMetadata(List<String> secrecyDocumentList, se.tekis.arende.Arende arende, Dokument doc, Attachment attachment) throws ApplicationException {
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

    private void setCaseEndDecision(List<Handelse> handelseList, ArchiveMetadata archiveMetadata) {
        List<Handelse> finalDecisionList = handelseList
                .stream().filter(h -> Constants.BYGGR_HANDELSESLAG_SLUTBESKED.equals(h.getHandelseslag()))
                .sorted(Comparator.comparing(Handelse::getStartDatum).reversed())
                .collect(Collectors.toList());

        // Use the latest
        if (!finalDecisionList.isEmpty()) {
            archiveMetadata.setCaseEndDecisionAt(finalDecisionList.get(0).getStartDatum());
        }
    }

    private void setPropertyFields(List<AbstractArendeObjekt> abstractArendeObjektList, ArchiveMetadata archiveMetadata) throws ApplicationException {
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

    private void setAttachmentCategory(Handling handling, Attachment attachment) {
        try {
            attachment.setCategory(AttachmentCategory.valueOf(handling.getTyp()));
        } catch (IllegalArgumentException e) {
            // All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL,
            // which means they get the archiveClassification D,
            // which means that they are not public in the archive.
            attachment.setCategory(AttachmentCategory.BIL);
        }
    }

    private List<String> getSecrecyDocumentsList(List<Handelse> handelseList) {
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
    private void setLowerExclusiveBoundWithReturnedValue(BatchFilter filter, ArendeBatch arendeBatch) {
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

    private LocalDateTime getStart(String searchStart) {
        LocalDateTime start;
        try {
            start = LocalDate.parse(searchStart).atStartOfDay();
        } catch (Exception e) {
            throw new BadRequestException(e.getLocalizedMessage());
        }

        return start;
    }

    private LocalDateTime getEnd(String searchEnd) {
        LocalDate end;
        try {
            if (searchEnd != null) {
                end = LocalDate.parse(searchEnd);

                if (end.isBefore(LocalDate.now())) {
                    return end.atTime(23, 59, 59);
                }
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getLocalizedMessage());
        }

        return LocalDateTime.now();
    }




}
