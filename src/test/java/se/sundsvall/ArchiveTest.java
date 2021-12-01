package se.sundsvall;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.BatchStatus;
import se.sundsvall.vo.BatchTrigger;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ArchiveTest {

    @Inject
    Archiver archiver;

    @InjectMock
    ArchiveDao archiveDao;

    @Test
    void testGetLatestCompletedBatchNoHit() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, BatchStatus.NOT_COMPLETED));
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, BatchStatus.NOT_COMPLETED));
        Mockito.when(archiveDao.getBatchHistory()).thenReturn(batchHistoryList);

        BatchHistory batchHistory = archiver.getLatestCompletedBatch();

        // No one is completed
        assertNull(batchHistory);
    }

    @Test
    void testGetLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, BatchStatus.NOT_COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, BatchStatus.COMPLETED);
        batchHistoryList.add(batchHistory2);

        Mockito.when(archiveDao.getBatchHistory()).thenReturn(batchHistoryList);

        BatchHistory latestBatchHistory = archiver.getLatestCompletedBatch();

        // Should be nr 2 because the latest is not completed
        assertEquals(batchHistory2.getEnd(), latestBatchHistory.getEnd());
    }

    @Test
    void testGetLatestCompletedBatch2() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, BatchStatus.COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, BatchStatus.COMPLETED);
        batchHistoryList.add(batchHistory2);

        Mockito.when(archiveDao.getBatchHistory()).thenReturn(batchHistoryList);

        BatchHistory latestBatchHistory = archiver.getLatestCompletedBatch();

        // Should be nr 1 because it is the latest
        assertEquals(batchHistory1.getEnd(), latestBatchHistory.getEnd());
    }

}
