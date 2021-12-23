package se.sundsvall;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sundsvall.archive.ArchiveResponse;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.casemanagement.*;
import se.sundsvall.sundsvall.messaging.MessagingService;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.BatchTrigger;
import se.sundsvall.vo.Status;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
class ArchiveTest {

    public static final String DOCUMENT_ID = "ABC123";
    @Inject
    Archiver archiver;

    @InjectMock
    ArchiveDao archiveDao;

    @InjectMock
    @RestClient
    CaseManagementService caseManagementServiceMock;

    @InjectMock
    @RestClient
    ArchiveService archiveServiceMock;

    @InjectMock
    @RestClient
    MessagingService messagingServiceMock;

    @BeforeEach
    void beforeEach() throws ServiceException {
        /*
        Mocks
         */

        // CaseManagement
        List<Attachment> attachmentList = new ArrayList<>();
        Attachment attachment_1 = new Attachment();
        ArchiveMetadata archiveMetadata = new ArchiveMetadata();
        archiveMetadata.setSystem(SystemType.BYGGR);
        archiveMetadata.setDocumentId(DOCUMENT_ID);
        attachment_1.setArchiveMetadata(archiveMetadata);
        attachment_1.setCategory(AttachmentCategory.ANS);
        attachment_1.setFile("dGVzdA==");
        attachment_1.setExtension(".pdf");
        attachment_1.setMimeType(null);
        attachment_1.setName("Filnamn 1");
        attachment_1.setNote("Anteckning 1");
        attachmentList.add(attachment_1);
        Mockito.when(caseManagementServiceMock.getDocuments(any(), any(), any())).thenReturn(attachmentList);

        // Archiver
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 123-123-123");
        Mockito.when(archiveServiceMock.postArchive(any())).thenReturn(archiveResponse);
    }

    // Standard scenario - Run batch for yesterday - 3 documents found
    // Standard scenario - Run batch for yesterday - 0 documents found
    // Try to run batch for the same date and verify it doesn't run
    // Run batch for attachmentCategory "GEO" and verify email was sent
    // Run batch and simulate request to CaseManagement failure. Verify we handle exception correctly and abort the batch.
    // Run batch for attachmentCategory "GEO" and simulate the request to Messaging fails. Verify we handle exception correctly and continue with the rest.
    // Run batch and simulate request to Archive failure. Verify we handle exception correctly and continue with the rest.
    // Try to run batch for future-date
    // Try to run batch with start date later than end date
    // Rerun a batch that failed and verify that the documents that was not completed gets completed and vice versa.

    @Test
    void runBatch() throws ApplicationException, ServiceException {
    LocalDate start = LocalDate.now().minusDays(1);
            LocalDate end =LocalDate.now();

        archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistory();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(1)).getArchiveHistories(any(), any());
        verify(archiveServiceMock, times(1)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());
        verify(archiveDao, times(1)).postArchiveHistory(any());
        verify(archiveDao, times(1)).updateArchiveHistory(any());
        verify(archiveDao, times(1)).updateBatchHistory(any());
    }

    @Test
    void runBatchGeotekniskUndersokning() throws ServiceException, ApplicationException {
        // CaseManagement mock
        List<Attachment> attachmentList = new ArrayList<>();
        Attachment attachment_1 = new Attachment();
        ArchiveMetadata archiveMetadata = new ArchiveMetadata();
        archiveMetadata.setSystem(SystemType.BYGGR);
        archiveMetadata.setDocumentId("ABC123");
        attachment_1.setArchiveMetadata(archiveMetadata);
        attachment_1.setCategory(AttachmentCategory.GEO);
        attachment_1.setFile("dGVzdA==");
        attachment_1.setExtension(".pdf");
        attachment_1.setMimeType(null);
        attachment_1.setName("Filnamn 1");
        attachment_1.setNote("Anteckning 1");
        attachmentList.add(attachment_1);
        Mockito.when(caseManagementServiceMock.getDocuments(any(), any(), any())).thenReturn(attachmentList);

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end =LocalDate.now();

        archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistory();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(1)).getArchiveHistories(any(), any());
        verify(archiveServiceMock, times(1)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());
        verify(archiveDao, times(1)).postArchiveHistory(any());
        verify(archiveDao, times(1)).updateArchiveHistory(any());
        verify(archiveDao, times(1)).updateBatchHistory(any());
        // TODO verify error message in the log
    }

    @Test
    void testErrorFromCaseManagement() throws ServiceException, ApplicationException {

        Mockito.when(caseManagementServiceMock.getDocuments(any(), any(), any())).thenThrow(ServiceException.create("{\"type\":\"https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4\",\"status\":404,\"title\":\"Not Found\",\"detail\":\"RESTEASY003210: Could not find resource for full path: http://microservices-test.sundsvall.se/cases/closed/documents/archive\"}", null, Response.Status.NOT_FOUND));

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end =LocalDate.now();

        ServiceException thrown = Assertions.assertThrows(ServiceException.class, () -> archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED));

        Assertions.assertEquals("{\"type\":\"https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4\",\"status\":404,\"title\":\"Not Found\",\"detail\":\"RESTEASY003210: Could not find resource for full path: http://microservices-test.sundsvall.se/cases/closed/documents/archive\"}", thrown.getLocalizedMessage());
        verify(archiveDao, times(1)).getBatchHistory();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(0)).getArchiveHistories(any(), any());
        verify(archiveServiceMock, times(0)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());
        verify(archiveDao, times(0)).postArchiveHistory(any());
        verify(archiveDao, times(0)).updateArchiveHistory(any());
        verify(archiveDao, times(0)).updateBatchHistory(any());
    }

    @Test
    void testErrorFromArchive() throws ServiceException, ApplicationException {
        Mockito.when(archiveServiceMock.postArchive(any())).thenThrow(ServiceException.create("{\n" +
                "  \"httpCode\": 500,\n" +
                "  \"message\": \"Service error\",\n" +
                "  \"technicalDetails\": {\n" +
                "    \"rootCode\": 500,\n" +
                "    \"rootCause\": \"Internal Server Error\",\n" +
                "    \"serviceId\": \"api-service-archive\",\n" +
                "    \"requestId\": null,\n" +
                "    \"details\": [\n" +
                "      \"Error invoking subclass method\",\n" +
                "      \"Request: /documents\"\n" +
                "    ]\n" +
                "  }\n" +
                "}", null, Response.Status.INTERNAL_SERVER_ERROR));

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end =LocalDate.now();

        archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistory();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(1)).getArchiveHistories(any(), any());
        verify(archiveServiceMock, times(1)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());
        verify(archiveDao, times(1)).postArchiveHistory(any());

        ArgumentCaptor<ArchiveHistory> archiveHistoryCaptor = ArgumentCaptor.forClass(ArchiveHistory.class);
        verify(archiveDao, times(1)).updateArchiveHistory(archiveHistoryCaptor.capture());
        ArchiveHistory persistedArchiveH = archiveHistoryCaptor.getValue();
        Assertions.assertEquals(Status.NOT_COMPLETED, persistedArchiveH.getStatus());

        verify(archiveDao, times(0)).updateBatchHistory(any());
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
