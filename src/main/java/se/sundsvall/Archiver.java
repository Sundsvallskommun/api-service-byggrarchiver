package se.sundsvall;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.archive.ArchiveService;
import se.sundsvall.casemanagement.Attachment;
import se.sundsvall.casemanagement.CaseManagementService;
import se.sundsvall.casemanagement.Information;
import se.sundsvall.casemanagement.SystemType;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchStatus;

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

    public void archiveByggrAttachments() {

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        BatchHistory latestBatch = getLatestBatch();

        BatchHistory newBatchHistory = new BatchHistory(start, end, BatchStatus.NOT_COMPLETED);

        if (latestBatch != null) {
            // If the latest batch end-date is today or later and the latest batch is completed
            if (!latestBatch.getEnd().isBefore(end)
                    && latestBatch.getBatchStatus().equals(BatchStatus.COMPLETED)) {
                log.info("The batch is already completed. Cancelling this batch...");
                return;
            }

            // If the latest batch end-date is before this batch start-date, we would risk to miss something.
            // Therefore - set the start-date to the latest batch end-date.
            if (latestBatch.getEnd().isBefore(start)) {
                log.info("It was a gap between the latest batch end-date and this batch start-date. Sets the start-date to: " + latestBatch.getEnd());
                start = latestBatch.getEnd();
            }

        }

        List<Attachment> attachmentList = new ArrayList<>();
        // Get Byggr-attachments from CaseManagement
        try {
            attachmentList = caseManagementService.getDocuments(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), SystemType.BYGGR);
        } catch (WebApplicationException e) {
            Information response;

            try {
                response = e.getResponse().readEntity(Information.class);
            } catch (Exception e1) {
                // Throw the primary exception
                throw e;
            }

            if (e.getResponse().getStatusInfo().equals(Response.Status.NOT_FOUND) && response.getDetail().equals("Documents not found")) {
                log.info("Status from CaseManagement.getDocuments = 404");
            } else {
                throw e;
            }
        }

        log.info("Runs batch with start-date: " + start + " and end-date: " + end);

        // Persist the start of this batch
        archiveDao.postBatchHistory(newBatchHistory);

        if (attachmentList.isEmpty()) {
            log.info("AttachmentList is empty - 0 attachments found in CaseManagement for ByggR.");
        } else {
            for (Attachment attachment : attachmentList) {
                List<ArchiveHistory> archiveHistories = archiveDao.getArchiveHistory(attachment.getId());

                if (archiveHistories.isEmpty()) {
                    ArchiveHistory archiveHistory = new ArchiveHistory();
                    archiveHistory.setSystem(attachment.getArchiveMetadata().getSystem());
                    archiveHistory.setDocumentId(attachment.getId());

                    // POST to archive
                    String s = archiveService.postArchive();
                    log.info(s);

                    archiveDao.postArchiveHistory(archiveHistory);
                } else {
                    log.info(attachment.getId() + " is already archived.");
                }
            }
        }

        // Persist that this batch is completed
        log.info("Batch completed.");
        newBatchHistory.setBatchStatus(BatchStatus.COMPLETED);
        archiveDao.updateBatchHistory(newBatchHistory);
    }

    private BatchHistory getLatestBatch() {
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistory();
        BatchHistory latestBatch = null;

        if (!batchHistoryList.isEmpty()) {
            batchHistoryList = batchHistoryList.stream()
                    .sorted(Comparator.comparing(BatchHistory::getEnd, Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            latestBatch = batchHistoryList.get(0);
            log.info("slutdatum på senaste batchen: " + latestBatch.getEnd());
        }

        return latestBatch;
    }
}
