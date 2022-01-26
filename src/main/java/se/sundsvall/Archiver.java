package se.sundsvall;

import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sokigo.arendeexport.ArendeExportIntegrationService;
import se.sundsvall.sokigo.arendeexport.ByggrMapper;
import se.sundsvall.sundsvall.archive.ArchiveMessage;
import se.sundsvall.sundsvall.archive.ArchiveResponse;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.messaging.MessagingService;
import se.sundsvall.sundsvall.messaging.vo.EmailRequest;
import se.sundsvall.sundsvall.messaging.vo.MessageStatusResponse;
import se.sundsvall.sundsvall.messaging.vo.Sender1;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.*;
import se.tekis.arende.Dokument;
import se.tekis.arende.HandelseHandling;
import se.tekis.arende.Handling;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.BatchFilter;
import vo.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class Archiver {

    @Inject
    Logger log;

    @Inject
    @RestClient
    ArchiveService archiveService;

    @Inject
    @RestClient
    MessagingService messagingService;

    @Inject
    ArchiveDao archiveDao;

    @Inject
    ByggrMapper byggrMapper;

    @Inject
    ArendeExportIntegrationService arendeExportIntegrationService;


    public BatchHistory reRunBatch(Long batchHistoryId) throws ApplicationException, ServiceException {
        BatchHistory batchHistory;
        try {
            batchHistory = archiveDao.getBatchHistory(batchHistoryId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getLocalizedMessage());
        }

        if (batchHistory == null) {
            throw new NotFoundException("Can't find BatchHistory with ID: " + batchHistoryId);
        }

        if (batchHistory.getStatus().equals(Status.COMPLETED)) {
            throw new BadRequestException(Constants.IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH);
        }

        log.info("Rerun batch: " + batchHistory);

        // Do the archiving
        return archive(batchHistory.getStart(), batchHistory.getEnd(), batchHistory);

    }

    public BatchHistory archiveByggrAttachments(LocalDate start, LocalDate end, BatchTrigger batchTrigger) throws ApplicationException, ServiceException {

        if (batchTrigger.equals(BatchTrigger.SCHEDULED)) {
            BatchHistory latestBatch = getLatestCompletedBatch();

            if (latestBatch != null) {
                // If this batch end-date is not after the latest batch end date, we don't need to run it again
                if (!end.isAfter(latestBatch.getEnd())) {
                    log.info("This batch does not have a later end-date(" + end + ") than the latest batch (" + latestBatch.getEnd() + "). Cancelling this batch...");
                    return null;
                }

                // If there is a gap between the latest batch end-date and this batch start-date, we would risk to miss something.
                // Therefore - set the start-date to the latest batch end-date, plus one day.
                if (start.isAfter(latestBatch.getEnd())) {
                    log.info("It was a gap between the latest batch end-date and this batch start-date. Sets the start-date to: " + latestBatch.getEnd().plusDays(1));
                    start = latestBatch.getEnd().plusDays(1);
                }

            }
        }

        // Persist the start of this batch
        BatchHistory batchHistory = new BatchHistory(start, end, batchTrigger, Status.NOT_COMPLETED);
        archiveDao.postBatchHistory(batchHistory);

        // Do the archiving
        return archive(start, end, batchHistory);
    }

    private BatchHistory archive(LocalDate searchStart, LocalDate searchEnd, BatchHistory batchHistory) throws ApplicationException {

        log.info("Runs batch: " + batchHistory.getId() + " with start-date: " + searchStart + " and end-date: " + searchEnd);

        // Holds the documents that have been processed
        List<ArchiveHistory> processedDocuments = new ArrayList<>();

        // Used for logging only
        List<se.tekis.arende.Arende> foundCases = new ArrayList<>();
        List<se.tekis.arende.Arende> foundClosedCases = new ArrayList<>();
        List<Dokument> foundDocuments = new ArrayList<>();

        LocalDateTime start = searchStart.atStartOfDay();
        LocalDateTime end = byggrMapper.getEnd(searchEnd);
        BatchFilter batchFilter = byggrMapper.getBatchFilter(start, end);

        ArendeBatch arendeBatch = null;


        do {
            byggrMapper.setLowerExclusiveBoundWithReturnedValue(batchFilter, arendeBatch);

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
                secrecyDocumentList = byggrMapper.getSecrecyDocumentsList(closedCase.getHandelseLista().getHandelse());

                List<HandelseHandling> handelseHandlingList = closedCase.getHandelseLista().getHandelse().stream()
                        .filter(handelse -> Constants.BYGGR_HANDELSETYP_ARKIV.equals(handelse.getHandelsetyp()))
                        .flatMap(handelse -> handelse.getHandlingLista().getHandling().stream())
                        .collect(Collectors.toList());

                for (Handling handling : handelseHandlingList) {
                    if (handling.getDokument() != null) {

                        String docId = handling.getDokument().getDokId();

                        ArchiveHistory oldArchiveHistory = archiveDao.getArchiveHistory(docId, SystemType.BYGGR);

                        // The new archiveHistory
                        ArchiveHistory newArchiveHistory = new ArchiveHistory();

                        if (oldArchiveHistory == null) {
                            log.info("The document " + docId + " does not exist in the db. Archive it..");

                            newArchiveHistory.setSystemType(SystemType.BYGGR);
                            newArchiveHistory.setDocumentId(docId);
                            newArchiveHistory.setBatchHistory(batchHistory);
                            newArchiveHistory.setStatus(Status.NOT_COMPLETED);
                            archiveDao.postArchiveHistory(newArchiveHistory);

                        } else if (oldArchiveHistory.getStatus().equals(Status.NOT_COMPLETED)) {
                            log.info("The document " + docId + " existed but has the status NOT_COMPLETED. Trying again...");

                            newArchiveHistory = oldArchiveHistory;
                            newArchiveHistory.setBatchHistory(batchHistory);

                        } else {
                            log.info("The document " + docId + " is already archived.");
                            continue;
                        }

                        // Get documents from byggr
                        List<Dokument> dokumentList = arendeExportIntegrationService.getDocument(docId);

                        if (dokumentList == null) {
                            throw new ApplicationException("The response from arendeExportIntegrationService.getDocument(" + docId + ") was null. Something went wrong...");
                        }

                        for (Dokument doc : dokumentList) {
                            foundDocuments.add(doc);
                            log.info("Document-Count: " + foundDocuments.size() + ". Found a document that should be archived - Case-ID: " + closedCase.getDnr() + " Document-ID: " + doc.getDokId() + " Document-name: " + doc.getNamn() + " Handling-ID: " + handling.getHandlingId() + " Handlingstyp: " + handling.getTyp());

                            Attachment attachment = new Attachment();
                            byggrMapper.setAttachmentFields(secrecyDocumentList, closedCase, handling, doc, attachment);

                            archiveAttachment(attachment, newArchiveHistory).ifPresent(processedDocuments::add);
                        }
                    }
                }
            }
        } while (batchFilter.getLowerExclusiveBound().isBefore(end));

        log.info("\nTotalt antal arenden: " + foundCases.size()
                + "\nAntal avslutade arenden: " + foundClosedCases.size()
                + "\nAntal dokument som ska arkiveras: " + foundDocuments.size());


        if (processedDocuments.stream().noneMatch(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED))) {
            // Persist that this batch is completed
            batchHistory.setStatus(Status.COMPLETED);
            archiveDao.updateBatchHistory(batchHistory);
        }

        log.info("Batch with ID: " + batchHistory.getId() + " is " + batchHistory.getStatus());

        return batchHistory;
    }

    private Optional<ArchiveHistory> archiveAttachment(Attachment attachment, ArchiveHistory newArchiveHistory) throws ApplicationException {

        ArchiveMessage archiveMessage = new ArchiveMessage();
        archiveMessage.setAttachment(attachment);

        String metadataXml;
        try {
            JAXBContext context = JAXBContext.newInstance(LeveransobjektTyp.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(new ObjectFactory().createLeveransobjekt(getLeveransobjektTyp(attachment)), stringWriter);
            metadataXml = stringWriter.toString();
            log.info(metadataXml);
        } catch (JAXBException e) {
            throw new ApplicationException("Something went wrong when trying to marshal LeveransobjektTyp", e);
        }

        archiveMessage.setMetadata(metadataXml);

        // Request to Archive
        ArchiveResponse archiveResponse = postArchive(archiveMessage);

        if (archiveResponse != null
                && archiveResponse.getArchiveId() != null) {
            // Success! Set status to completed
            newArchiveHistory.setStatus(Status.COMPLETED);
            newArchiveHistory.setArchiveId(archiveResponse.getArchiveId());

            if (attachment.getCategory().equals(AttachmentCategory.GEO)) {
                MessageStatusResponse response = sendEmailToLantmateriet(attachment, newArchiveHistory);
            }
        } else {
            // Not successful... Set status to not completed
            newArchiveHistory.setStatus(Status.NOT_COMPLETED);
        }

        archiveDao.updateArchiveHistory(newArchiveHistory);

        return Optional.of(newArchiveHistory);
    }

    private LeveransobjektTyp getLeveransobjektTyp(Attachment attachment) {
        LeveransobjektTyp leveransobjekt = new LeveransobjektTyp();
        leveransobjekt.setArkivbildarStruktur(getArkivbildarStruktur());
        leveransobjekt.setArkivobjektListaArenden(getArkivobjektListaArenden(attachment));

        // TODO - I don't know what to set these fields to right now
        leveransobjekt.setInformationsklass(null);
        leveransobjekt.setVerksamhetsbaseradArkivredovisning(null);
        leveransobjekt.setSystemInfo(null);
        leveransobjekt.setArkivobjektListaHandlingar(null);

        return leveransobjekt;
    }

    private ArkivobjektListaArendenTyp getArkivobjektListaArenden(Attachment attachment) {
        ArkivobjektArendeTyp arkivobjektArende = new ArkivobjektArendeTyp();

        arkivobjektArende.setArkivobjektID(attachment.getArchiveMetadata().getCaseId());
        arkivobjektArende.setArendemening(attachment.getArchiveMetadata().getCaseTitle());
        arkivobjektArende.setAvslutat(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getCaseEndedAt()));
        arkivobjektArende.setSkapad(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getCaseCreatedAt()));
        arkivobjektArende.setStatusArende(StatusArendeEnum.STÄNGT);
        arkivobjektArende.getFastighet().add(getFastighet(attachment));
        arkivobjektArende.setArkivobjektListaHandlingar(getArkivobjektListaHandlingar(attachment));

        // TODO - lägg till detta i metadata
        arkivobjektArende.setArendeTyp(null);

        // TODO - Not sure of this one...
        arkivobjektArende.getKlass().add("F2 Bygglov");

        // TODO - I don't know what to set these fields to right now
        arkivobjektArende.setInkommen(null);
        arkivobjektArende.setInformationsklass(null);
        arkivobjektArende.setArkiverat(null);
        arkivobjektArende.setBeskrivning(null);
        arkivobjektArende.setArkiverat(null);
        arkivobjektArende.setAtkomst(null);
        arkivobjektArende.setExpedierad(null);
        arkivobjektArende.setForvaringsenhetsReferens(null);
        arkivobjektArende.setGallring(null);
        arkivobjektArende.setMinaArendeoversikterKlassificering(null);
        arkivobjektArende.setMinaArendeoversikterStatus(null);
        arkivobjektArende.setNotering(null);
        arkivobjektArende.setSistaAnvandandetidpunkt(null);
        arkivobjektArende.setSystemidentifierare(null);
        arkivobjektArende.setUpprattad(null);

        ArkivobjektListaArendenTyp arkivobjektListaArendenTyp = new ArkivobjektListaArendenTyp();
        arkivobjektListaArendenTyp.getArkivobjektArende().add(arkivobjektArende);
        return arkivobjektListaArendenTyp;
    }

    private ArkivobjektListaHandlingarTyp getArkivobjektListaHandlingar(Attachment attachment) {
        ArkivobjektHandlingTyp arkivobjektHandling = new ArkivobjektHandlingTyp();
        arkivobjektHandling.setArkivobjektID(attachment.getArchiveMetadata().getDocumentId());
        arkivobjektHandling.setHandlingstyp(attachment.getArchiveMetadata().getArchiveClassification());
        arkivobjektHandling.setRubrik(attachment.getCategory().getDescription());
        arkivobjektHandling.setSkapad(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getDocumentCreatedAt()));
        arkivobjektHandling.getBilaga().add(getBilaga(attachment));

        // TODO - I don't know what to set this fields to right now
        arkivobjektHandling.setInformationsklass(null);
        arkivobjektHandling.setInkommen(null);
        arkivobjektHandling.setAtkomst(null);
        arkivobjektHandling.setAvsandare(null);
        arkivobjektHandling.setBeskrivning(null);
        arkivobjektHandling.setExpedierad(null);
        arkivobjektHandling.setForvaringsenhetsReferens(null);
        arkivobjektHandling.setGallring(null);
        arkivobjektHandling.setLopnummer(null);
        arkivobjektHandling.setNotering(null);
        arkivobjektHandling.setSistaAnvandandetidpunkt(null);
        arkivobjektHandling.setSkannad(null);
        arkivobjektHandling.setStatusHandling(null);
        arkivobjektHandling.setSystemidentifierare(null);
        arkivobjektHandling.setUpprattad(null);

        ArkivobjektListaHandlingarTyp arkivobjektListaHandlingarTyp = new ArkivobjektListaHandlingarTyp();
        arkivobjektListaHandlingarTyp.getArkivobjektHandling().add(arkivobjektHandling);
        return arkivobjektListaHandlingarTyp;
    }

    private BilagaTyp getBilaga(Attachment attachment) {
        BilagaTyp bilaga = new BilagaTyp();
        bilaga.setNamn(attachment.getName());
        bilaga.setBeskrivning(attachment.getNote());
        bilaga.setMimetyp(attachment.getMimeType());
        return bilaga;
    }

    private FastighetTyp getFastighet(Attachment attachment) {
        FastighetTyp fastighet = new FastighetTyp();
        fastighet.setFastighetsbeteckning(attachment.getArchiveMetadata().getPropertyDesignation());
        fastighet.setTrakt(attachment.getArchiveMetadata().getRegion());
        fastighet.setObjektidentitet(attachment.getArchiveMetadata().getRegisterUnit());
        return fastighet;
    }

    private ArkivbildarStrukturTyp getArkivbildarStruktur() {
        ArkivbildarStrukturTyp arkivbildarStruktur = new ArkivbildarStrukturTyp();

        ArkivbildareTyp arkivbildareSundsvallsKommun = new ArkivbildareTyp();
        arkivbildareSundsvallsKommun.setNamn("Sundsvalls kommun");
        arkivbildareSundsvallsKommun.setVerksamhetstidFran("1974");

        ArkivbildareTyp arkivbildareByggOchMiljoNamnden = new ArkivbildareTyp();
        arkivbildareByggOchMiljoNamnden.setNamn("Bygg- och miljönämnden");
        arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1974");
        arkivbildareSundsvallsKommun.setArkivbildare(arkivbildareByggOchMiljoNamnden);

        arkivbildarStruktur.setArkivbildare(arkivbildareSundsvallsKommun);
        return arkivbildarStruktur;
    }

    private String formatToIsoDateOrReturnNull(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    private String formatToIsoDateOrReturnNull(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    private ArchiveResponse postArchive(ArchiveMessage archiveMessage) {
        ArchiveResponse archiveResponse = null;
        // POST to archive
        try {
            log.info("Framtida anrop till archive");
            archiveResponse = archiveService.postArchive(archiveMessage);
            log.info("Response from archive: " + archiveResponse);
        } catch (ServiceException e) {
            // Just log the error and continue with the rest
            log.error("Unexpected response from Archive when using method archiveService.postArchive -->\nHTTP Status: " + e.getStatus().getStatusCode() + " " + e.getStatus().getReasonPhrase() + "\nResponse body: " + e.getMessage());
        }
        return archiveResponse;
    }


    public BatchHistory getLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistories();

        // Filter completed batches
        batchHistoryList = batchHistoryList.stream()
                .filter(b -> b.getStatus().equals(Status.COMPLETED))
                .collect(Collectors.toList());

        // Sort by end-date of batch
        batchHistoryList = batchHistoryList.stream()
                .sorted(Comparator.comparing(BatchHistory::getEnd, Comparator.reverseOrder()))
                .collect(Collectors.toList());


        BatchHistory latestBatch = null;

        if (!batchHistoryList.isEmpty()) {

            // Get the latest batch
            latestBatch = batchHistoryList.get(0);
            log.info("The latest batch: " + latestBatch);
        }

        return latestBatch;
    }

    private MessageStatusResponse sendEmailToLantmateriet(Attachment attachment, ArchiveHistory archiveHistory) throws ApplicationException {

        EmailRequest emailRequest = new EmailRequest();

        // Email-attachment
        se.sundsvall.sundsvall.messaging.vo.Attachment emailAttachment = new se.sundsvall.sundsvall.messaging.vo.Attachment();
        emailAttachment.setName(attachment.getName());
        emailAttachment.setContent(attachment.getFile());
        emailAttachment.setContentType(attachment.getMimeType());
        emailRequest.setAttachments(List.of(emailAttachment));

        // Sender
        Sender1 sender1 = new Sender1();
        sender1.setEmailAddress("dennis.nilsson@sundsvall.se");
        sender1.setName("Archiver");
        emailRequest.setSender(sender1);

        emailRequest.setEmailAddress("dennis.nilsson@b3.se");
        emailRequest.setSubject("Arkiverad geoteknisk handling");

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("archiveId", getStringOrEmpty(archiveHistory.getArchiveId()));
        valuesMap.put("archiveUrl", getStringOrEmpty(archiveHistory.getArchiveUrl()));
        valuesMap.put("byggrCaseId", getStringOrEmpty(attachment.getArchiveMetadata().getCaseId()));
        valuesMap.put("byggrDocumentName", getStringOrEmpty(attachment.getName()));
        valuesMap.put("byggrDocumentId", getStringOrEmpty(archiveHistory.getDocumentId()));

        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        String htmlWithReplacedValues = stringSubstitutor.replace(Constants.LANTMATERIET_HTML_TEMPLATE);

        log.info("HTML:\n" + htmlWithReplacedValues);

        emailRequest.setHtmlMessage(Base64.getEncoder().encodeToString(htmlWithReplacedValues.getBytes()));

        MessageStatusResponse response;
        try {
            response = messagingService.postEmail(emailRequest);

            if (response != null
                    && response.isSent()) {
                log.info("E-mail sent to Lantmäteriet with information about geoteknisk handling for ArchiveId: " + archiveHistory.getArchiveId() + " MessageId: " + response.getMessageId());
            } else {
                log.error("Something went wrong when trying to send e-mail about geoteknisk handling to Lantmäteriet." +
                        "\nArchiveId: " + archiveHistory.getArchiveId() + "" +
                        "\nDocumentId in Byggr: " + attachment.getArchiveMetadata().getDocumentId() + "" +
                        "\nResponse from messaging: " + response);
            }

        } catch (ServiceException e) {
            throw new ApplicationException("The request to messagingService.postEmail failed", e);
        }
        return response;
    }

    private String getStringOrEmpty(String string) {
        return string != null ? string : "";
    }
}
