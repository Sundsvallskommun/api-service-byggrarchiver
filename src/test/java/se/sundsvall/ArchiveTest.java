package se.sundsvall;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sundsvall.archive.ArchiveResponse;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.casemanagement.*;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.Status;
import se.sundsvall.vo.BatchTrigger;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ArchiveTest {

    @Inject
    Archiver archiver;

    @InjectMock
    ArchiveDao archiveDao;

    @InjectMock
    @RestClient
    CaseManagementService caseManagementService;

    @InjectMock
    @RestClient
    ArchiveService archiveService;

    @BeforeEach
    void beforeEach() throws ServiceException {
        /*
        Mocks
         */

        // CaseManagement
        List<Attachment> attachmentList = new ArrayList<>();
        Attachment attachment_1 = new Attachment();
        attachment_1.setArchiveMetadata(new ArchiveMetadata(SystemType.BYGGR));
        attachment_1.setCategory(AttachmentCategory.APPLICATION);
        attachment_1.setFile("dGVzdA==");
        attachment_1.setExtension(".pdf");
        attachment_1.setId("ABC123");
        attachment_1.setMimeType(null);
        attachment_1.setName("Filnamn 1");
        attachment_1.setNote("Anteckning 1");
        attachmentList.add(attachment_1);
        Mockito.when(caseManagementService.getDocuments(any(), any(), any())).thenReturn(attachmentList);

        // Archiver
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 123-123-123");
        Mockito.when(archiveService.postArchive(any())).thenReturn(archiveResponse);
    }

    @Test
    void runBatch() throws ApplicationException, ServiceException {

        archiver.archiveByggrAttachments(LocalDate.now().minusDays(1), LocalDate.now(), BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistory();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementService, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(1)).getArchiveHistory(any(), any());
        verify(archiveService, times(1)).postArchive(any());
        verify(archiveDao, times(1)).postArchiveHistory(any());
        verify(archiveDao, times(1)).updateArchiveHistory(any());
    }

    @Test
    void testGetLatestCompletedBatchNoHit() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.NOT_COMPLETED));
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.NOT_COMPLETED));
        Mockito.when(archiveDao.getBatchHistory()).thenReturn(batchHistoryList);

        BatchHistory batchHistory = archiver.getLatestCompletedBatch();

        // No one is completed
        assertNull(batchHistory);
    }

    @Test
    void testGetLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.NOT_COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory2);

        Mockito.when(archiveDao.getBatchHistory()).thenReturn(batchHistoryList);

        BatchHistory latestBatchHistory = archiver.getLatestCompletedBatch();

        // Should be nr 2 because the latest is not completed
        assertEquals(batchHistory2.getEnd(), latestBatchHistory.getEnd());
    }

    @Test
    void testGetLatestCompletedBatch2() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory2);

        Mockito.when(archiveDao.getBatchHistory()).thenReturn(batchHistoryList);

        BatchHistory latestBatchHistory = archiver.getLatestCompletedBatch();

        // Should be nr 1 because it is the latest
        assertEquals(batchHistory1.getEnd(), latestBatchHistory.getEnd());
    }

}
