package se.sundsvall;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.core.cli.Argument;
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
import se.sundsvall.sundsvall.messaging.vo.MessageStatusResponse;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.BatchTrigger;
import se.sundsvall.vo.Status;

import javax.activation.MimeType;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class ArchiveTest {

    public static final String DOCUMENT_ID_1 = "ABC123";
    public static final String DOCUMENT_ID_2 = "aaaaaaaaaaaaaaaaabbbbbbbbbbbccccccccccc";
    public static final String DOCUMENT_ID_3 = "12345678";
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

        Attachment attachment_1 = new Attachment();
        ArchiveMetadata archiveMetadata_1 = new ArchiveMetadata();
        archiveMetadata_1.setSystem(SystemType.BYGGR);
        archiveMetadata_1.setDocumentId(DOCUMENT_ID_1);
        attachment_1.setArchiveMetadata(archiveMetadata_1);
        attachment_1.setCategory(AttachmentCategory.ANS);
        attachment_1.setFile("dGVzdA==");
        attachment_1.setExtension(".pdf");
        attachment_1.setMimeType(null);
        attachment_1.setName("Filnamn 1");
        attachment_1.setNote("Anteckning 1");

        Attachment attachment_2 = new Attachment();
        ArchiveMetadata archiveMetadata_2 = new ArchiveMetadata();
        archiveMetadata_2.setSystem(SystemType.BYGGR);
        archiveMetadata_2.setDocumentId(DOCUMENT_ID_2);
        attachment_2.setArchiveMetadata(archiveMetadata_2);
        attachment_2.setCategory(AttachmentCategory.GEO);
        attachment_2.setFile("dGVzdA==");
        attachment_2.setExtension(".docx");
        attachment_2.setMimeType("application/msword");
        attachment_2.setName("Filnamn_2");
        attachment_2.setNote("Anteckning 2");

        Attachment attachment_3 = new Attachment();
        ArchiveMetadata archiveMetadata_3 = new ArchiveMetadata();
        archiveMetadata_3.setSystem(SystemType.BYGGR);
        archiveMetadata_3.setDocumentId(DOCUMENT_ID_3);
        attachment_3.setArchiveMetadata(archiveMetadata_3);
        attachment_3.setCategory(AttachmentCategory.ANMÄ);
        attachment_3.setFile("dGVzdA==");
        attachment_3.setExtension(".pdf");
        attachment_3.setMimeType("application/pdf");
        attachment_3.setName("Filnamn-3");
        attachment_3.setNote("Anteckning 3");

        List<Attachment> attachmentList = List.of(attachment_1, attachment_2, attachment_3);
        Mockito.when(caseManagementServiceMock.getDocuments(any(), any(), any())).thenReturn(attachmentList);

        // Messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("b9535bce-fed9-4a42-a8b7-6fb6540aa3f3");
        messageStatusResponse.setSent(true);
        Mockito.when(messagingServiceMock.postEmail(any())).thenReturn(messageStatusResponse);

        // Archiver
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 123-123-123");
        Mockito.when(archiveServiceMock.postArchive(any())).thenReturn(archiveResponse);
    }

    // TODO - create all this tests
    // Try to run batch for the same date and verify it doesn't run
    // Run batch for attachmentCategory "GEO" and verify email was sent
    // Run batch and simulate request to CaseManagement failure. Verify we handle exception correctly and abort the batch.
    // Run batch and simulate request to Archive failure. Verify we handle exception correctly and continue with the rest.
    // Try to run batch for future-date
    // Try to run batch with start date later than end date
    // Rerun a batch that failed and verify that the documents that was not completed gets completed and vice versa.

    // Standard scenario - Run batch for yesterday - 3 documents found
    @Test
    void testStandardBatchThreeDocsFound() throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistories();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(3)).getArchiveHistory(any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());
        verify(archiveDao, times(3)).postArchiveHistory(any());
        verify(archiveDao, times(3)).updateArchiveHistory(any());
        verify(archiveDao, times(1)).updateBatchHistory(any());
    }

    // Standard scenario - Run batch for yesterday - 0 documents found
    @Test
    void testStandardBatchNoDocsFound() throws ServiceException, ApplicationException {
        when(caseManagementServiceMock.getDocuments(any(),any(),any())).thenReturn(new ArrayList<>());

        LocalDate yesterday = LocalDate.now().minusDays(1);
        archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistories();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(0)).getArchiveHistory(any(), any());
        verify(archiveServiceMock, times(0)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());
        verify(archiveDao, times(0)).postArchiveHistory(any());
        verify(archiveDao, times(0)).updateArchiveHistory(any());

        ArgumentCaptor<BatchHistory> batchHistoryArgumentCaptor = ArgumentCaptor.forClass(BatchHistory.class);
        verify(archiveDao, times(1)).updateBatchHistory(batchHistoryArgumentCaptor.capture());
        Assertions.assertEquals(Status.COMPLETED, batchHistoryArgumentCaptor.getValue().getStatus());
    }

    // Run batch for attachmentCategory "GEO" and simulate the email was not sent. Verify we log the error and persist all.
    @Test
    void runBatchGeotekniskUndersokningMessageSentFalse() throws ServiceException, ApplicationException {
        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setSent(false);
        Mockito.when(messagingServiceMock.postEmail(any())).thenReturn(messageStatusResponse);

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistories();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(3)).getArchiveHistory(any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());
        verify(archiveDao, times(3)).postArchiveHistory(any());

        ArgumentCaptor<ArchiveHistory> archiveHistoryArgumentCaptor = ArgumentCaptor.forClass(ArchiveHistory.class);
        verify(archiveDao, times(3)).updateArchiveHistory(archiveHistoryArgumentCaptor.capture());
        archiveHistoryArgumentCaptor.getAllValues().forEach(archiveHistory -> Assertions.assertEquals(Status.COMPLETED, archiveHistory.getStatus()));

        verify(archiveDao, times(1)).updateBatchHistory(any());
        // TODO verify error message in the log
    }

    @Test
    void testErrorFromCaseManagement() throws ServiceException, ApplicationException {

        Mockito.when(caseManagementServiceMock.getDocuments(any(), any(), any())).thenThrow(ServiceException.create("{\"type\":\"https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4\",\"status\":404,\"title\":\"Not Found\",\"detail\":\"RESTEASY003210: Could not find resource for full path: http://microservices-test.sundsvall.se/cases/closed/documents/archive\"}", null, Response.Status.NOT_FOUND));

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        ServiceException thrown = Assertions.assertThrows(ServiceException.class, () -> archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED));

        Assertions.assertEquals("{\"type\":\"https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4\",\"status\":404,\"title\":\"Not Found\",\"detail\":\"RESTEASY003210: Could not find resource for full path: http://microservices-test.sundsvall.se/cases/closed/documents/archive\"}", thrown.getLocalizedMessage());
        verify(archiveDao, times(1)).getBatchHistories();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(0)).getArchiveHistory(any(), any());
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
        LocalDate end = LocalDate.now();

        archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        verify(archiveDao, times(1)).getBatchHistories();
        verify(archiveDao, times(1)).postBatchHistory(any());
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveDao, times(1)).getArchiveHistory(any(), any());
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
        Mockito.when(archiveDao.getBatchHistories()).thenReturn(batchHistoryList);

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

        Mockito.when(archiveDao.getBatchHistories()).thenReturn(batchHistoryList);

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

        Mockito.when(archiveDao.getBatchHistories()).thenReturn(batchHistoryList);

        BatchHistory latestBatchHistory = archiver.getLatestCompletedBatch();

        // Should be nr 1 because it is the latest
        assertEquals(batchHistory1.getEnd(), latestBatchHistory.getEnd());
    }

}
