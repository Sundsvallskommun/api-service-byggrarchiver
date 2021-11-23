package se.sundsvall;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.sundsvall.casemanagement.Attachment;
import se.sundsvall.casemanagement.CaseManagementService;
import se.sundsvall.casemanagement.SystemType;
import se.sundsvall.vo.ArchiveBatchHistory;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    ArchiveDao archiveDao;

    public void archiveByggrAttachments() {

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        List<ArchiveBatchHistory> archiveBatchHistoryList = archiveDao.getArchiveBatchHistory();
        archiveBatchHistoryList = archiveBatchHistoryList.stream().sorted(Comparator.comparing(ArchiveBatchHistory::getEnd, Comparator.reverseOrder())).collect(Collectors.toList());

        log.info("slutdatum på senaste batchen: " + archiveBatchHistoryList.get(0).getEnd());

        ArchiveBatchHistory latestBatch = archiveBatchHistoryList.get(0);

        if (!latestBatch.getEnd().isBefore(end) && latestBatch.getBatchStatus().equals(BatchStatus.COMPLETED))
        {
            log.info("Batch är redan körd och är komplett. Avbryter.");
            return;
        }

        if (latestBatch.getEnd().isBefore(start)) {
            log.info("Det var ett tag sedan batchen kördes. Sätter start till: " + latestBatch.getEnd());
            start = latestBatch.getEnd();
        }

        log.info("Kör batch med start: " + start + " och slut: " + end);

        ArchiveBatchHistory archiveBatchHistory = new ArchiveBatchHistory(start, end, BatchStatus.NOT_COMPLETED);
        archiveDao.postArchiveBatchHistory(archiveBatchHistory);

        List<Attachment> attachmentList = caseManagementService.getDocuments(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), SystemType.BYGGR);

        if (attachmentList.isEmpty()) {
            log.info("Inga dokument ska arkiveras.");
        }
        for (Attachment attachment : attachmentList) {
            log.info(attachment.getName());

            List<ArchiveHistory> archiveHistories = archiveDao.getArchiveHistory(attachment.getId());

            if (archiveHistories.isEmpty()) {
                ArchiveHistory archiveHistory = new ArchiveHistory();
                archiveHistory.setSystem(attachment.getArchiveMetadata().getSystem());
                archiveHistory.setDocumentId(attachment.getId());

                archiveDao.postArchiveHistory(archiveHistory);
            } else {
                log.info(attachment.getId() + " är redan arkiverad.");
            }
        }

        log.info("Uppdatera batchHistory");
        archiveBatchHistory.setBatchStatus(BatchStatus.COMPLETED);
        archiveDao.updateArchiveBatchHistory(archiveBatchHistory);
    }
}
