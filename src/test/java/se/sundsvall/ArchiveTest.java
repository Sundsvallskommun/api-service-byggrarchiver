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
    void testGetLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, BatchStatus.NOT_COMPLETED));
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, BatchStatus.NOT_COMPLETED));
        Mockito.when(archiveDao.getBatchHistory()).thenReturn(batchHistoryList);

        BatchHistory batchHistory = archiver.getLatestCompletedBatch();

        assertNull(batchHistory);
    }

}
