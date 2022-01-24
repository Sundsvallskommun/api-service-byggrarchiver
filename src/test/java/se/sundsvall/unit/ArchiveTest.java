package se.sundsvall.unit;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import se.sundsvall.ArchiveDao;
import se.sundsvall.Archiver;
import se.sundsvall.TestDao;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sundsvall.archive.ArchiveMessage;
import se.sundsvall.sundsvall.archive.ArchiveResponse;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.casemanagement.*;
import se.sundsvall.sundsvall.messaging.MessagingService;
import se.sundsvall.sundsvall.messaging.vo.MessageStatusResponse;
import se.sundsvall.unit.support.ArchiveMessageAttachmentMatcher;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.BatchTrigger;
import se.sundsvall.vo.Status;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class ArchiveTest {

    public static final String DOCUMENT_ID_1 = "ABC123";
    public static final String DOCUMENT_ID_2 = "aaaaaaaaaaaaaaaaabbbbbbbbbbbccccccccccc";
    public static final String DOCUMENT_ID_3 = "12345678";
    public static final String POST_ARCHIVE_EXCEPTION_MESSAGE = "{\n" +
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
            "}";

    @Inject
    TestDao testDao;

    @Inject
    Archiver archiver;

    @Inject
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
        // Clear db between tests
        testDao.deleteAllFromAllTables();

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

    // Standard scenario - Run batch for yesterday - 3 documents found
    @Test
    void testStandardBatchThreeDocsFound() throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory returnedBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());

        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(returnedBatchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(returnedBatchHistory));
        Assertions.assertEquals(3, archiveDao.getArchiveHistories(returnedBatchHistory.getId()).size());
    }

    // Standard scenario - Run batch for yesterday - 0 documents found
    @Test
    void testStandardBatchNoDocsFound() throws ServiceException, ApplicationException {
        when(caseManagementServiceMock.getDocuments(any(), any(), any())).thenReturn(new ArrayList<>());

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory returnedBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(0)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());

        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(returnedBatchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(returnedBatchHistory));
        Assertions.assertEquals(0, archiveDao.getArchiveHistories(returnedBatchHistory.getId()).size());
    }

    // Try to run scheduled batch for the same date and verify it doesn't run
    @Test
    void testRunScheduledBatchForSameDate() throws ServiceException, ApplicationException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);
        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(firstBatchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(firstBatchHistory));
        Assertions.assertEquals(3, archiveDao.getArchiveHistories(firstBatchHistory.getId()).size());

        // Run second batch with the same date
        BatchHistory secondBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);
        Assertions.assertNull(secondBatchHistory);

        // Only the first batch
        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());
    }

    // Try to run manual batch for the same date and verify it runs
    @Test
    void testRunManualBatchForSameDate() throws ServiceException, ApplicationException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);
        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(firstBatchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(firstBatchHistory));
        Assertions.assertEquals(3, archiveDao.getArchiveHistories(firstBatchHistory.getId()).size());

        BatchHistory secondBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.MANUAL);

        Assertions.assertEquals(2, archiveDao.getBatchHistories().size());
        Assertions.assertEquals(3, archiveDao.getArchiveHistories().size());
        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(secondBatchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(secondBatchHistory));
        Assertions.assertEquals(0, archiveDao.getArchiveHistories(secondBatchHistory.getId()).size());

        // Both first and second batch
        verify(caseManagementServiceMock, times(2)).getDocuments(any(), any(), any());
        // Only the first batch
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());
    }

    // Try to run batch for a date back in time and verify the scheduled batch change the startDate back in time to the day after latest scheduled batch.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapScheduledThenScheduled(BatchTrigger batchTrigger) throws ServiceException, ApplicationException {
        LocalDate aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.archiveByggrAttachments(aLongTimeAgo, aLongTimeAgo, batchTrigger);
        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(firstBatchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(firstBatchHistory));
        Assertions.assertEquals(3, archiveDao.getArchiveHistories(firstBatchHistory.getId()).size());

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory secondBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.SCHEDULED);

        Assertions.assertTrue(archiveDao.getBatchHistories().contains(secondBatchHistory));
        Assertions.assertEquals(aLongTimeAgo.plusDays(1), secondBatchHistory.getStart());
        Assertions.assertEquals(yesterday, secondBatchHistory.getEnd());
        Assertions.assertEquals(Status.COMPLETED, secondBatchHistory.getStatus());
    }

    // Try to run batch for a date back in time and verify the manual batch does NOT change the startDate back in time.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapManualThenScheduled(BatchTrigger batchTrigger) throws ServiceException, ApplicationException {
        LocalDate aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.archiveByggrAttachments(aLongTimeAgo, aLongTimeAgo, batchTrigger);
        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(firstBatchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(firstBatchHistory));
        Assertions.assertEquals(3, archiveDao.getArchiveHistories(firstBatchHistory.getId()).size());

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory secondBatchHistory = archiver.archiveByggrAttachments(yesterday, yesterday, BatchTrigger.MANUAL);

        Assertions.assertTrue(archiveDao.getBatchHistories().contains(secondBatchHistory));
        Assertions.assertEquals(yesterday, secondBatchHistory.getStart());
        Assertions.assertEquals(yesterday, secondBatchHistory.getEnd());
        Assertions.assertEquals(Status.COMPLETED, secondBatchHistory.getStatus());
    }

    // Run batch for attachmentCategory "GEO" and simulate the email was not sent. Verify we log the error and persist all.
    @Test
    void runBatchGeotekniskUndersokningMessageSentFalse() throws ServiceException, ApplicationException {
        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("12312-3123-123-123-123");
        messageStatusResponse.setSent(false);
        Mockito.when(messagingServiceMock.postEmail(any())).thenReturn(messageStatusResponse);

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        BatchHistory batchHistory = archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(batchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(batchHistory));
        Assertions.assertEquals(3, archiveDao.getArchiveHistories(batchHistory.getId()).size());
        archiveDao.getArchiveHistories(batchHistory.getId()).forEach(archiveHistory -> Assertions.assertEquals(Status.COMPLETED, archiveHistory.getStatus()));

        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());

        // TODO verify error message in the log
    }

    // Run batch for attachmentCategory "GEO" and verify email was sent
    @Test
    void runBatchGeotekniskUndersokningMessageSentTrue() throws ServiceException, ApplicationException {
        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("12312-3123-123-123-123");
        messageStatusResponse.setSent(true);
        Mockito.when(messagingServiceMock.postEmail(any())).thenReturn(messageStatusResponse);

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        BatchHistory batchHistory = archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        Assertions.assertEquals(Status.COMPLETED, archiveDao.getBatchHistory(batchHistory.getId()).getStatus());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(batchHistory));
        Assertions.assertEquals(3, archiveDao.getArchiveHistories(batchHistory.getId()).size());
        archiveDao.getArchiveHistories(batchHistory.getId()).forEach(archiveHistory -> Assertions.assertEquals(Status.COMPLETED, archiveHistory.getStatus()));

        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(1)).postEmail(any());
    }

    // Run batch and simulate request to CaseManagement failure. Verify we handle exception correctly and abort the batch.
    @Test
    void testErrorFromCaseManagement() throws ServiceException {

        String exceptionMessage = "{\"type\":\"https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4\",\"status\":404,\"title\":\"Not Found\",\"detail\":\"RESTEASY003210: Could not find resource for full path: http://microservices-test.sundsvall.se/cases/closed/documents/archive\"}";
        Mockito.when(caseManagementServiceMock.getDocuments(any(), any(), any())).thenThrow(ServiceException.create(exceptionMessage, null, Response.Status.NOT_FOUND));

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        BatchHistory batchHistory;
        ServiceException thrown = Assertions.assertThrows(ServiceException.class, () -> archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED));

        Assertions.assertEquals(exceptionMessage, thrown.getLocalizedMessage());
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistories();
        Assertions.assertEquals(1, batchHistoryList.size());
        Assertions.assertEquals(Status.NOT_COMPLETED, archiveDao.getBatchHistory(batchHistoryList.get(0).getId()).getStatus());
        Assertions.assertEquals(0, archiveDao.getArchiveHistories().size());

        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(0)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());
    }

    // Run batch and simulate request to Archive failure. Verify we handle exception correctly and continue with the rest.
    @Test
    void testErrorFromArchive() throws ServiceException, ApplicationException {

        doThrow(ServiceException.create(POST_ARCHIVE_EXCEPTION_MESSAGE, null, Response.Status.INTERNAL_SERVER_ERROR)).when(archiveServiceMock).postArchive(any());

        // Test
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        BatchHistory batchHistory = archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        Assertions.assertEquals(Status.NOT_COMPLETED, archiveDao.getBatchHistory(batchHistory.getId()).getStatus());
        List<ArchiveHistory> archiveHistoryList = archiveDao.getArchiveHistories(batchHistory.getId());
        Assertions.assertEquals(3, archiveHistoryList.size());
        archiveHistoryList.forEach(archiveHistory -> Assertions.assertEquals(Status.NOT_COMPLETED, archiveHistory.getStatus()));

        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());
    }

    // Rerun an earlier not_completed batch - GET batchhistory and verify it was completed
    @Test
    void testReRunNotCompletedBatch() throws ServiceException, ApplicationException {

        // The same object as the CaseManagement mock
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

        ArchiveMessage archiveMessage = new ArchiveMessage();
        archiveMessage.setAttachment(attachment_2);

        doThrow(ServiceException.create(POST_ARCHIVE_EXCEPTION_MESSAGE, null, Response.Status.INTERNAL_SERVER_ERROR)).when(archiveServiceMock).postArchive(argThat(new ArchiveMessageAttachmentMatcher(archiveMessage)));

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        // First run, fails
        BatchHistory firstBatchHistory = archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);

        Assertions.assertEquals(1, archiveDao.getBatchHistories().size());
        Assertions.assertEquals(Status.NOT_COMPLETED, archiveDao.getBatchHistory(firstBatchHistory.getId()).getStatus());
        List<ArchiveHistory> firstArchiveHistoryList = archiveDao.getArchiveHistories(firstBatchHistory.getId());
        Assertions.assertEquals(3, firstArchiveHistoryList.size());
        // Only one should be NOT_COMPLETED, the other two should be COMPLETED
        List <ArchiveHistory> firstNotCompletedArchiveHistories = firstArchiveHistoryList.stream().filter(archiveHistory -> Status.NOT_COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        List <ArchiveHistory> firstCompletedArchiveHistories = firstArchiveHistoryList.stream().filter(archiveHistory -> Status.COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        Assertions.assertEquals(1, firstNotCompletedArchiveHistories.size());
        Assertions.assertEquals(2, firstCompletedArchiveHistories.size());

        System.out.println("1: " + firstNotCompletedArchiveHistories.get(0));

        verify(caseManagementServiceMock, times(1)).getDocuments(any(), any(), any());
        verify(archiveServiceMock, times(3)).postArchive(any());
        verify(messagingServiceMock, times(0)).postEmail(any());

        // ReRun, success
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 123-123-123");
        doReturn(archiveResponse).when(archiveServiceMock).postArchive(any());

        BatchHistory reRunBatchHistory = archiver.reRunBatch(firstBatchHistory.getId());
        Assertions.assertEquals(firstBatchHistory.getId(), reRunBatchHistory.getId());
        Assertions.assertEquals(Status.COMPLETED, reRunBatchHistory.getStatus());
        Assertions.assertEquals(1, archiveDao.getBatchHistories().size());

        List<ArchiveHistory> reRunArchiveHistoryList = archiveDao.getArchiveHistories(reRunBatchHistory.getId());
        Assertions.assertEquals(3, reRunArchiveHistoryList.size());
        // Now all should be COMPLETED
        List <ArchiveHistory> reRunNotCompletedArchiveHistories = reRunArchiveHistoryList.stream().filter(archiveHistory -> Status.NOT_COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        List <ArchiveHistory> reRunCompletedArchiveHistories = reRunArchiveHistoryList.stream().filter(archiveHistory -> Status.COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        Assertions.assertEquals(0, reRunNotCompletedArchiveHistories.size());
        Assertions.assertEquals(3, reRunCompletedArchiveHistories.size());

        // Both the first batch and the reRun
        verify(caseManagementServiceMock, times(2)).getDocuments(any(), any(), any());
        // 3 the first time + 1 in the reRun
        verify(archiveServiceMock, times(4)).postArchive(any());
        // Only in the rerun when the archiving of GEO success
        verify(messagingServiceMock, times(1)).postEmail(any());
    }

    @Test
    void testGetLatestCompletedBatchNoHit() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.NOT_COMPLETED));
        batchHistoryList.add(new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.NOT_COMPLETED));
        batchHistoryList.forEach(batchHistory -> archiveDao.postBatchHistory(batchHistory));

        BatchHistory batchHistory = archiver.getLatestCompletedBatch();

        // No one is completed
        Assertions.assertNull(batchHistory);
    }

    @Test
    void testGetLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.NOT_COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory2);

        batchHistoryList.forEach(batchHistory -> archiveDao.postBatchHistory(batchHistory));

        BatchHistory latestBatchHistory = archiver.getLatestCompletedBatch();

        // Should be nr 2 because the latest is not completed
        Assertions.assertEquals(batchHistory2.getEnd(), latestBatchHistory.getEnd());
    }

    @Test
    void testGetLatestCompletedBatch2() {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory2);

        batchHistoryList.forEach(batchHistory -> archiveDao.postBatchHistory(batchHistory));

        BatchHistory latestBatchHistory = archiver.getLatestCompletedBatch();

        // Should be nr 1 because it is the latest
        Assertions.assertEquals(batchHistory1.getEnd(), latestBatchHistory.getEnd());
    }

}
