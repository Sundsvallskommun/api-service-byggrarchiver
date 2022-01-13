package se.sundsvall.unit;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.sundsvall.ArchiveDao;
import se.sundsvall.sundsvall.casemanagement.SystemType;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.Status;
import se.sundsvall.vo.BatchTrigger;

import javax.inject.Inject;
import java.time.LocalDate;

@QuarkusTest
class ArchiveHistoryDaoTest {

    @Inject
    ArchiveDao archiveDao;

    @Test
    void testPostAndGetArchiveHistory() throws ApplicationException {

        BatchHistory batchHistory = new BatchHistory(LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.COMPLETED);
        archiveDao.postBatchHistory(batchHistory);

        ArchiveHistory archiveHistory = new ArchiveHistory();
        archiveHistory.setDocumentId("abc-123");
        archiveHistory.setSystemType(SystemType.BYGGR);
        archiveHistory.setStatus(Status.COMPLETED);
        archiveHistory.setArchiveId("123-123-4123-1231");
        archiveHistory.setBatchHistory(batchHistory);

        archiveDao.postArchiveHistory(archiveHistory);

        ArchiveHistory result = archiveDao.getArchiveHistory(archiveHistory.getDocumentId(), archiveHistory.getSystemType());
        Assertions.assertEquals(archiveHistory.getDocumentId(), result.getDocumentId());
        Assertions.assertEquals(archiveHistory.getSystemType(), result.getSystemType());
        Assertions.assertEquals(archiveHistory.getStatus(), result.getStatus());
    }

    @Test
    void testPostAndGetArchiveBatchHistory() {
        BatchHistory batchHistory = new BatchHistory();
        batchHistory.setStart(LocalDate.now().minusDays(1));
        batchHistory.setEnd(LocalDate.now());
        batchHistory.setStatus(Status.NOT_COMPLETED);
        batchHistory.setBatchTrigger(BatchTrigger.SCHEDULED);

        archiveDao.postBatchHistory(batchHistory);

        BatchHistory result = archiveDao.getBatchHistory(batchHistory.getId());
        Assertions.assertEquals(batchHistory, result);
    }


}