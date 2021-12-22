package se.sundsvall;

import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
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
import se.sundsvall.vo.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
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


    public void reRunBatch(Long batchHistoryId) throws ApplicationException, ServiceException {
        BatchHistory batchHistory;
        try {
            batchHistory = archiveDao.getBatchHistory(batchHistoryId);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(new Information(Constants.RFC_LINK_NOT_FOUND,
                            Response.Status.NOT_FOUND.getReasonPhrase(),
                            Response.Status.NOT_FOUND.getStatusCode(),
                            e.getLocalizedMessage(), null))
                    .build());
        }

        if (batchHistory.getStatus().equals(Status.COMPLETED)) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Information(Constants.RFC_LINK_BAD_REQUEST,
                            Response.Status.BAD_REQUEST.getReasonPhrase(),
                            Response.Status.BAD_REQUEST.getStatusCode(),
                            "It's not possible to rerun a completed batch.", null))
                    .build());
        }

        log.info("Rerun batch: " + batchHistory);

        // Do the archiving
        archive(batchHistory.getStart(), batchHistory.getEnd(), batchHistory);

    }

    public List<ArchiveHistory> archiveByggrAttachments(LocalDate start, LocalDate end, BatchTrigger batchTrigger) throws ApplicationException, ServiceException {

        if (batchTrigger.equals(BatchTrigger.SCHEDULED)) {
            BatchHistory latestBatch = getLatestCompletedBatch();

            if (latestBatch != null) {
                // If this batch end-date is not after the latest batch end date, we don't need to run it again
                if (!end.isAfter(latestBatch.getEnd())) {
                    log.info("The batch is already done. Cancelling this batch...");
                    return new ArrayList<>();
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

    private List<ArchiveHistory> archive(LocalDate start, LocalDate end, BatchHistory batchHistory) throws ApplicationException, ServiceException {

        log.info("Runs batch: " + batchHistory.getId() + " with start-date: " + start + " and end-date: " + end);

        // Get Byggr-attachments from CaseManagement
        List<Attachment> attachmentList = getByggrAttachments(start, end);

        if (attachmentList.isEmpty()) {
            log.info("AttachmentList is empty - 0 attachments found in CaseManagement for ByggR.");
            return new ArrayList<>();
        } else {
            List<ArchiveHistory> archivedList = new ArrayList<>();

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

                // Request to Archive
                ArchiveResponse archiveResponse = postArchive(attachment);

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

                archivedList.add(newArchiveHistory);
            }

            if (archivedList.stream().noneMatch(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED))) {
                // Persist that this batch is completed
                log.info("Batch completed.");
                batchHistory.setStatus(Status.COMPLETED);
                archiveDao.updateBatchHistory(batchHistory);
            }

            return archivedList;
        }
    }

    private ArchiveResponse postArchive(Attachment attachment) {
        ArchiveResponse archiveResponse = null;
        // POST to archive
        try {
            log.info("Framtida anrop till archive");
            archiveResponse = archiveService.postArchive(attachment);
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
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistory();

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
            log.info("slutdatum på senaste batchen: " + latestBatch.getEnd());
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
            }
            else {
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
