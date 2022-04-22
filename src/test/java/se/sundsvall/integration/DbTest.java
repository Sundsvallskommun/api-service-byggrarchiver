package se.sundsvall.integration;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.api.model.Status;
import se.sundsvall.byggrarchiver.api.model.BatchTrigger;

import javax.inject.Inject;
import java.time.LocalDate;

@QuarkusTest
class DbTest {

    @Inject
    ArchiveHistoryRepository archiveHistoryRepository;

    @Inject
    BatchHistoryRepository batchHistoryRepository;

    @Test
    void testPostAndGetArchiveHistory() throws ApplicationException {

        BatchHistory batchHistory = new BatchHistory(LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryRepository.postBatchHistory(batchHistory);

        ArchiveHistory archiveHistory = new ArchiveHistory();
        archiveHistory.setDocumentId("abc-123");
        archiveHistory.setCaseId("BYGG 123-123");
        archiveHistory.setStatus(Status.COMPLETED);
        archiveHistory.setArchiveId("123-123-4123-1231");
        archiveHistory.setBatchHistory(batchHistory);

        archiveHistoryRepository.postArchiveHistory(archiveHistory);

        ArchiveHistory result = archiveHistoryRepository.getArchiveHistory(archiveHistory.getDocumentId(), archiveHistory.getCaseId());
        Assertions.assertEquals(archiveHistory.getDocumentId(), result.getDocumentId());
        Assertions.assertEquals(archiveHistory.getStatus(), result.getStatus());
    }

    @Test
    void testPostAndGetArchiveBatchHistory() {
        BatchHistory batchHistory = new BatchHistory();
        batchHistory.setStart(LocalDate.now().minusDays(1));
        batchHistory.setEnd(LocalDate.now());
        batchHistory.setStatus(Status.NOT_COMPLETED);
        batchHistory.setBatchTrigger(BatchTrigger.SCHEDULED);

        batchHistoryRepository.postBatchHistory(batchHistory);

        BatchHistory result = batchHistoryRepository.getBatchHistory(batchHistory.getId());
        Assertions.assertEquals(batchHistory, result);
    }


}