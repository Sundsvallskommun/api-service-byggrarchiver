package se.sundsvall;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.sundsvall.casemanagement.SystemType;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchStatus;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ArchiveHistoryDaoTest {

    @Inject
    ArchiveDao archiveDao;

    @Test
    public void testPostAndGetArchiveHistory() {
        ArchiveHistory archiveHistory = new ArchiveHistory();
        archiveHistory.setDocumentId("abc-123");
        archiveHistory.setSystem(SystemType.BYGGR);

        archiveDao.postArchiveHistory(archiveHistory);

        List<ArchiveHistory> resultList = archiveDao.getArchiveHistory(archiveHistory.getDocumentId());
        Assertions.assertEquals(1, resultList.size());
    }

    @Test
    public void testPostAndGetArchiveBatchHistory() {
        BatchHistory batchHistory = new BatchHistory();
        batchHistory.setStart(LocalDate.now().minusDays(1));
        batchHistory.setEnd(LocalDate.now());
        batchHistory.setBatchStatus(BatchStatus.NOT_COMPLETED);

        archiveDao.postArchiveBatchHistory(batchHistory);

        BatchHistory result = archiveDao.getArchiveBatchHistory(batchHistory.getId());
        Assertions.assertEquals(batchHistory, result);
    }


}