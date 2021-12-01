package se.sundsvall;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.sundsvall.sundsvall.casemanagement.SystemType;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.BatchStatus;

import javax.inject.Inject;
import java.time.LocalDate;

@QuarkusTest
class ArchiveHistoryDaoTest {

    @Inject
    ArchiveDao archiveDao;

    @Test
    void testPostAndGetArchiveHistory() throws ApplicationException {
        ArchiveHistory archiveHistory = new ArchiveHistory();
        archiveHistory.setDocumentId("abc-123");
        archiveHistory.setSystemType(SystemType.BYGGR);
        archiveHistory.setStatus(BatchStatus.COMPLETED);

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
        batchHistory.setBatchStatus(BatchStatus.NOT_COMPLETED);

        archiveDao.postBatchHistory(batchHistory);

        BatchHistory result = archiveDao.getBatchHistory(batchHistory.getId());
        Assertions.assertEquals(batchHistory, result);
    }


}