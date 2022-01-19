package se.sundsvall;

import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sundsvall.archive.ArchiveMessage;
import se.sundsvall.sundsvall.archive.ArchiveResponse;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.casemanagement.Attachment;
import se.sundsvall.sundsvall.casemanagement.AttachmentCategory;
import se.sundsvall.sundsvall.casemanagement.CaseManagementService;
import se.sundsvall.sundsvall.casemanagement.SystemType;
import se.sundsvall.sundsvall.messaging.MessagingService;
import se.sundsvall.sundsvall.messaging.vo.EmailRequest;
import se.sundsvall.sundsvall.messaging.vo.MessageStatusResponse;
import se.sundsvall.sundsvall.messaging.vo.Sender1;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.BatchTrigger;
import se.sundsvall.vo.Status;
import vo.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
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
    CaseManagementService caseManagementService;

    @Inject
    @RestClient
    ArchiveService archiveService;

    @Inject
    @RestClient
    MessagingService messagingService;

    @Inject
    ArchiveDao archiveDao;


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

    private BatchHistory archive(LocalDate start, LocalDate end, BatchHistory batchHistory) throws ApplicationException, ServiceException {

        log.info("Runs batch: " + batchHistory.getId() + " with start-date: " + start + " and end-date: " + end);

        // Get Byggr-attachments from CaseManagement
        List<Attachment> attachmentList = getByggrAttachments(start, end);

        // Holds the documents that have been processed
        List<ArchiveHistory> processedDocuments = new ArrayList<>();

        if (attachmentList.isEmpty()) {
            log.info("AttachmentList is empty - 0 attachments found in CaseManagement for ByggR.");
        } else {
            for (Attachment attachment : attachmentList) {
                ArchiveHistory oldArchiveHistory = archiveDao.getArchiveHistory(attachment.getArchiveMetadata().getDocumentId(), attachment.getArchiveMetadata().getSystem());

                // The new archiveHistory
                ArchiveHistory newArchiveHistory;

                if (oldArchiveHistory == null) {
                    log.info("The document " + attachment.getArchiveMetadata().getDocumentId() + " does not exist in the db. Archive it..");

                    newArchiveHistory = new ArchiveHistory();
                    newArchiveHistory.setSystemType(attachment.getArchiveMetadata().getSystem());
                    newArchiveHistory.setDocumentId(attachment.getArchiveMetadata().getDocumentId());
                    newArchiveHistory.setBatchHistory(batchHistory);
                    newArchiveHistory.setStatus(Status.NOT_COMPLETED);
                    archiveDao.postArchiveHistory(newArchiveHistory);

                } else if (oldArchiveHistory.getStatus().equals(Status.NOT_COMPLETED)) {
                    log.info("The document " + attachment.getArchiveMetadata().getDocumentId() + " existed but has the status NOT_COMPLETED. Trying again...");

                    newArchiveHistory = oldArchiveHistory;
                    newArchiveHistory.setBatchHistory(batchHistory);

                } else {
                    log.info("The document " + attachment.getArchiveMetadata().getDocumentId() + " is already archived.");
                    continue;
                }

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

                processedDocuments.add(newArchiveHistory);
            }
        }

        if (processedDocuments.stream().noneMatch(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED))) {
            // Persist that this batch is completed
            log.info("Batch completed.");
            batchHistory.setStatus(Status.COMPLETED);
            archiveDao.updateBatchHistory(batchHistory);
        }

        return archiveDao.getBatchHistory(batchHistory.getId());
    }

    private LeveransobjektTyp getLeveransobjektTyp(Attachment attachment) {
        LeveransobjektTyp leveransobjekt = new LeveransobjektTyp();
        leveransobjekt.setArkivbildarStruktur(getArkivbildarStrukturTyp());
        ArkivobjektListaArendenTyp arkivobjektListaArendenTyp = new ArkivobjektListaArendenTyp();
        arkivobjektListaArendenTyp.getArkivobjektArende().add(getArkivobjektArendeTyp(attachment));
        leveransobjekt.setArkivobjektListaArenden(arkivobjektListaArendenTyp);

        // TODO - I don't know what to set this fields to right now
        leveransobjekt.setInformationsklass(attachment.getArchiveMetadata().getArchiveClassification());
        leveransobjekt.setVerksamhetsbaseradArkivredovisning(null);
        leveransobjekt.setSystemInfo(null);
        leveransobjekt.setArkivobjektListaHandlingar(null);

        return leveransobjekt;
    }

    private ArkivobjektArendeTyp getArkivobjektArendeTyp(Attachment attachment) {
        ArkivobjektArendeTyp arkivobjektArende = new ArkivobjektArendeTyp();

        arkivobjektArende.setArkivobjektID(attachment.getArchiveMetadata().getCaseId());
        arkivobjektArende.setArendemening(attachment.getArchiveMetadata().getCaseTitle());
        arkivobjektArende.setAvslutat(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getCaseEndedAt()));
        arkivobjektArende.setInkommen(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getCaseCreatedAt()));
        arkivobjektArende.setSkapad(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getCaseCreatedAt()));
        arkivobjektArende.setStatusArende(StatusArendeEnum.STÄNGT);

        arkivobjektArende.getFastighet().add(getFastighet(attachment));

        arkivobjektArende.setArkivobjektListaHandlingar(getArkivobjektListaHandlingar(attachment));

        // TODO - I don't know what to set this fields to right now
        arkivobjektArende.setArendeTyp(null);
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

        return arkivobjektArende;
    }

    private ArkivobjektListaHandlingarTyp getArkivobjektListaHandlingar(Attachment attachment) {
        ArkivobjektHandlingTyp arkivobjektHandling = new ArkivobjektHandlingTyp();
        arkivobjektHandling.setArkivobjektID(attachment.getArchiveMetadata().getDocumentId());
        arkivobjektHandling.setInformationsklass(attachment.getArchiveMetadata().getArchiveClassification());
        arkivobjektHandling.setHandlingstyp(attachment.getCategory().getDescription());
        arkivobjektHandling.setRubrik(attachment.getName());
        arkivobjektHandling.setInkommen(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getDocumentCreatedAt()));
        arkivobjektHandling.setSkapad(formatToIsoDateOrReturnNull(attachment.getArchiveMetadata().getDocumentCreatedAt()));

        ArkivobjektListaHandlingarTyp arkivobjektListaHandlingarTyp = new ArkivobjektListaHandlingarTyp();
        arkivobjektListaHandlingarTyp.getArkivobjektHandling().add(arkivobjektHandling);
        return arkivobjektListaHandlingarTyp;
    }

    private FastighetTyp getFastighet(Attachment attachment) {
        FastighetTyp fastighet = new FastighetTyp();
        fastighet.setFastighetsbeteckning(attachment.getArchiveMetadata().getPropertyDesignation());
        fastighet.setTrakt(attachment.getArchiveMetadata().getRegion());
        fastighet.setObjektidentitet(attachment.getArchiveMetadata().getRegisterUnit());
        return fastighet;
    }

    private ArkivbildarStrukturTyp getArkivbildarStrukturTyp() {
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


    public List<Attachment> getByggrAttachments(LocalDate start, LocalDate end) throws ServiceException {
        List<Attachment> attachmentList = new ArrayList<>();
        try {
            attachmentList = caseManagementService.getDocuments(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), SystemType.BYGGR);
        } catch (ServiceException e) {
            if (e.getStatus().equals(Response.Status.NOT_FOUND)
                    && e.getMessage() != null
                    && e.getMessage().contains("Documents not found")) {
                log.info("Status from CaseManagement.getDocuments: HTTP 404 - Documents not found");
            } else {
                throw e;
            }
        }
        return attachmentList;
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
