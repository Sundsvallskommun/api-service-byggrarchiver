package se.sundsvall;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.sundsvall.archive.ArchiveResponse;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.casemanagement.Attachment;
import se.sundsvall.sundsvall.casemanagement.CaseManagementService;
import se.sundsvall.sundsvall.casemanagement.SystemType;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.vo.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    ArchiveDao archiveDao;


    public void archiveByggrAttachments(LocalDate start, LocalDate end, BatchTrigger batchTrigger) throws ApplicationException {

        if (batchTrigger.equals(BatchTrigger.SCHEDULED)) {
            BatchHistory latestBatch = getLatestCompletedBatch();

            if (latestBatch != null) {
                // If the latest batch end-date is today or later we don't need to run it again
                if (!latestBatch.getEnd().isBefore(end)) {
                    log.info("The batch is already done. Cancelling this batch...");
                    return;
                }

                // If the latest batch end-date is before this batch start-date, we would risk to miss something.
                // Therefore - set the start-date to the latest batch end-date.
                if (latestBatch.getEnd().isBefore(start)) {
                    log.info("It was a gap between the latest batch end-date and this batch start-date. Sets the start-date to: " + latestBatch.getEnd());
                    start = latestBatch.getEnd();
                }

            }
        }

        log.info("Runs batch with start-date: " + start + " and end-date: " + end);

        // Persist the start of this batch
        BatchHistory newBatchHistory = new BatchHistory(start, end, batchTrigger, BatchStatus.NOT_COMPLETED);
        archiveDao.postBatchHistory(newBatchHistory);

        // Get Byggr-attachments from CaseManagement
        List<Attachment> attachmentList = getByggrAttachments(start, end);

        // Post archives to archive
        archive(attachmentList, newBatchHistory);

        // Persist that this batch is completed
        log.info("Batch completed.");
        newBatchHistory.setBatchStatus(BatchStatus.COMPLETED);
        archiveDao.updateBatchHistory(newBatchHistory);
    }

    private void archive(List<Attachment> attachmentList, BatchHistory batchHistory) throws ApplicationException {
        if (attachmentList.isEmpty()) {
            log.info("AttachmentList is empty - 0 attachments found in CaseManagement for ByggR.");
        } else {
            for (Attachment attachment : attachmentList) {
                ArchiveHistory oldArchiveHistory = archiveDao.getArchiveHistory(attachment.getId(), attachment.getArchiveMetadata().getSystem());

                if (oldArchiveHistory == null) {
                    log.info("The document does not exist in the db. Archive it..");

                    ArchiveHistory newArchiveHistory = new ArchiveHistory();
                    newArchiveHistory.setSystemType(attachment.getArchiveMetadata().getSystem());
                    newArchiveHistory.setDocumentId(attachment.getId());
                    newArchiveHistory.setBatchHistory(batchHistory);
                    newArchiveHistory.setStatus(BatchStatus.COMPLETED);

                    // POST to archive
                    try {
                        log.info("Framtida anrop till archive");
                        ArchiveResponse archiveResponse = archiveService.postArchive(attachment);
                        log.info("Response from archive: " + archiveResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        newArchiveHistory.setStatus(BatchStatus.NOT_COMPLETED);
                    }

                    archiveDao.postArchiveHistory(newArchiveHistory);
                } else if (oldArchiveHistory.getStatus().equals(BatchStatus.NOT_COMPLETED)) {
                    log.info("The document existed but has the status NOT_COMPLETED. Trying again...");
                    oldArchiveHistory.setStatus(BatchStatus.COMPLETED);
                    oldArchiveHistory.setBatchHistory(batchHistory);

                    try {
                        log.info("POST oldArchive");
                    } catch (Exception e) {
                        oldArchiveHistory.setStatus(BatchStatus.NOT_COMPLETED);
                    }

                    archiveDao.updateArchiveHistory(oldArchiveHistory);

                } else {
                    log.info("The document " + attachment.getId() + " is already archived.");
                }
            }
        }
    }


    public List<Attachment> getByggrAttachments(LocalDate start, LocalDate end) {
        List<Attachment> attachmentList = new ArrayList<>();
        try {
            attachmentList = caseManagementService.getDocuments(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), SystemType.BYGGR);
        } catch (ServiceException e) {
            if (e.getStatus().equals(Response.Status.NOT_FOUND)
                    && e.getMessage() != null
                    && e.getMessage().contains("Documents not found")) {
                log.info("Status from CaseManagement.getDocuments: HTTP 404 - Documents not found");
            } else {
                log.error("Unexpected response from CaseManagement when using method caseManagementService.getDocuments -->\nHTTP Status: " + e.getStatus().getStatusCode() + " " + e.getStatus().getReasonPhrase() + "\nResponse body: " + e.getMessage());

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new Information(Constants.RFC_LINK_INTERNAL_SERVER_ERROR,
                                Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                Constants.ERR_MSG_EXTERNAL_SERVICE, null))
                        .build());
            }
        }
        return attachmentList;
    }

    public BatchHistory getLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistory();

        // Filter completed batches
        batchHistoryList = batchHistoryList.stream()
                .filter(b -> b.getBatchStatus().equals(BatchStatus.COMPLETED))
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
}
