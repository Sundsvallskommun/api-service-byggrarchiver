package se.sundsvall.unit;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import generated.se.sundsvall.messaging.MessageStatusResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import se.sundsvall.ArchiveDao;
import se.sundsvall.Archiver;
import se.sundsvall.TestDao;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sokigo.CaseUtil;
import se.sundsvall.sokigo.arendeexport.ArendeExportIntegrationService;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.messaging.MessagingService;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.*;
import se.tekis.arende.*;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.ArrayOfArende;
import se.tekis.servicecontract.BatchFilter;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
class ArchiveTest {

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
    public static final String ONGOING = "Pågående";

    @Inject
    TestDao testDao;

    @Inject
    Archiver archiver;

    @Inject
    ArchiveDao archiveDao;

    @Inject
    CaseUtil caseUtil;

    @InjectMock
    @RestClient
    ArchiveService archiveServiceMock;

    @InjectMock
    @RestClient
    MessagingService messagingServiceMock;

    @InjectMock
    ArendeExportIntegrationService arendeExportIntegrationService;

    /**
     * Util method for creating arende-objects
     *
     * @param status               - status for the arende
     * @param handelsetyp          - type of handelse that should be included
     * @param attachmentCategories - the documents that should be generated
     * @return Arende
     */
    private Arende createArendeObject(String status, String handelsetyp, List<AttachmentCategory> attachmentCategories) {
        Arende arende = new Arende();
        arende.setDnr("BYGG 2021-" + new Random().nextInt(999999));

        arende.setStatus(status);
        Handelse handelse = new Handelse();
        handelse.setHandelsetyp(handelsetyp);

        ArrayOfHandelseHandling arrayOfHandelseHandling = new ArrayOfHandelseHandling();
        List<Dokument> dokumentList = new ArrayList<>();
        attachmentCategories.forEach(category -> {
            Dokument dokument = new Dokument();
            dokument.setDokId(String.valueOf(new Random().nextInt(999999)));
            dokument.setNamn("Test filnamn");
            DokumentFil docFil = new DokumentFil();
            docFil.setFilAndelse("pdf");
            dokument.setFil(docFil);
            dokument.setSkapadDatum(LocalDateTime.now().minusDays(30));

            dokumentList.add(dokument);

            HandelseHandling handling = new HandelseHandling();
            handling.setTyp(category.name());
            handling.setDokument(dokument);

            arrayOfHandelseHandling.getHandling().add(handling);
        });

        handelse.setHandlingLista(arrayOfHandelseHandling);
        ArrayOfHandelse arrayOfHandelse = new ArrayOfHandelse();
        arrayOfHandelse.getHandelse().add(handelse);
        arende.setHandelseLista(arrayOfHandelse);

        for (Dokument doc : dokumentList) {
            Mockito.doReturn(List.of(doc)).when(arendeExportIntegrationService).getDocument(doc.getDokId());
        }

        return arende;

    }

    @BeforeEach
    void beforeEach() throws ServiceException {
        // Clear db between tests
        testDao.deleteAllFromAllTables();

        /*
        Mocks
         */

        // ArendeExport

        // Default mock for ArendeExportIntegrationService
        ArendeBatch arendeBatch = new ArendeBatch();
        ArrayOfArende arrayOfArende = new ArrayOfArende();
        arendeBatch.setArenden(arrayOfArende);
        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(any());

        // Messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("b9535bce-fed9-4a42-a8b7-6fb6540aa3f3");
        messageStatusResponse.setSent(true);
        Mockito.doReturn(messageStatusResponse).when(messagingServiceMock).postEmail(any());

        // Archiver
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 123-123-123");
        Mockito.doReturn(archiveResponse).when(archiveServiceMock).postArchive(any());
    }

    // Standard scenario - Run batch for yesterday - 0 cases and documents found
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch0Cases0Docs(BatchTrigger batchTrigger) throws ServiceException, ApplicationException {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(25, 0, 0, 0);

        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Cases0Docs(BatchTrigger batchTrigger) throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, new ArrayList<>());
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 0, 0, 0);

        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 0);
    }

    // GetDocument returns empty list for one of the documents
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Cases3DocsGetDocumentReturnsEmpty(BatchTrigger batchTrigger) throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Return empty document-list for one document
        Mockito.doReturn(new ArrayList<>()).when(arendeExportIntegrationService).getDocument(arende1.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling().get(1).getDokument().getDokId());

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 3, 2, 0);
        verifyBatchHistory(returnedBatchHistory, Status.NOT_COMPLETED, 3);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1CaseWithWrongStatus3Docs(BatchTrigger batchTrigger) throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(ONGOING, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 0, 0, 0);

        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch2Cases1WithWrongHandelseslag2Docs(BatchTrigger batchTrigger) throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, "BESLUT", List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        Arende arende2 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.LUTE, AttachmentCategory.RUE));
        arrayOfArende.getArende().addAll(List.of(arende1, arende2));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 2, 2, 0);
        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 2);
    }


    // Standard scenario - Run batch for yesterday - 1 case and 3 documents found
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Case3Docs(BatchTrigger batchTrigger) throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(3, 3, 3, 0);

        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 3);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch3Cases1Ended1Doc(BatchTrigger batchTrigger) throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.TOMTPLBE));
        Arende arende2 = createArendeObject(ONGOING, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        Arende arende3 = createArendeObject(ONGOING, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1, arende2, arende3));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 1, 1, 0);

        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 1);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch3Cases2Ended4Docs(BatchTrigger batchTrigger) throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.TOMTPLBE));
        Arende arende2 = createArendeObject(ONGOING, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        Arende arende3 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1, arende2, arende3));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 4, 4, 0);
        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 4);
    }


    // Try to run scheduled batch for the same date and verify it doesn't run
    @Test
    void testRunScheduledBatchForSameDate() throws ServiceException, ApplicationException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);
        verifyBatchHistory(firstBatchHistory, Status.COMPLETED, 3);

        // Run second batch with the same date
        BatchHistory secondBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);
        Assertions.assertNull(secondBatchHistory);

        // Only the first batch
        verifyCalls(2, 3, 3, 0);
    }

    // Try to run manual batch for the same date and verify it runs
    @Test
    void testRunManualBatchForSameDate() throws ServiceException, ApplicationException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);
        verifyBatchHistory(firstBatchHistory, Status.COMPLETED, 3);

        BatchHistory secondBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.MANUAL);

        Assertions.assertEquals(2, archiveDao.getBatchHistories().size());
        Assertions.assertEquals(3, archiveDao.getArchiveHistories().size());
        verifyBatchHistory(secondBatchHistory, Status.COMPLETED, 0);

        // Both first and second batch
        verifyCalls(4, 3, 3, 0);
    }

    // Try to run batch for a date back in time and verify the scheduled batch change the startDate back in time to the day after latest scheduled batch.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapScheduledThenScheduled(BatchTrigger batchTrigger) throws ApplicationException {
        LocalDate aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);
        verifyBatchHistory(firstBatchHistory, Status.COMPLETED, 0);

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory secondBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyBatchHistory(secondBatchHistory, Status.COMPLETED, 0);

        Assertions.assertEquals(aLongTimeAgo.plusDays(1), secondBatchHistory.getStart());
        Assertions.assertEquals(yesterday, secondBatchHistory.getEnd());

    }

    // Try to run batch for a date back in time and verify the manual batch does NOT change the startDate back in time.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapManualThenScheduled(BatchTrigger batchTrigger) throws ApplicationException {
        LocalDate aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        BatchHistory firstBatchHistory = archiver.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);
        verifyBatchHistory(firstBatchHistory, Status.COMPLETED, 0);

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory secondBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.MANUAL);

        verifyBatchHistory(secondBatchHistory, Status.COMPLETED, 0);

        Assertions.assertEquals(yesterday, secondBatchHistory.getStart());
        Assertions.assertEquals(yesterday, secondBatchHistory.getEnd());
    }

    @Test
    void testGetLatestCompletedBatch() throws ApplicationException {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED, Status.NOT_COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory2);

        batchHistoryList.forEach(batchHistory -> archiveDao.postBatchHistory(batchHistory));

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory latestBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyBatchHistory(latestBatchHistory, Status.COMPLETED, 0);

        // Should be nr 2 because the latest is not completed
        Assertions.assertEquals(batchHistory2.getEnd().plusDays(1), latestBatchHistory.getStart());
        Assertions.assertEquals(yesterday, latestBatchHistory.getEnd());

    }

    @Test
    void testGetLatestCompletedBatch2() throws ApplicationException {
        List<BatchHistory> batchHistoryList = new ArrayList<>();

        BatchHistory batchHistory1 = new BatchHistory(LocalDate.now().minusDays(5), LocalDate.now().minusDays(3), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory1);

        BatchHistory batchHistory2 = new BatchHistory(LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), BatchTrigger.SCHEDULED, Status.COMPLETED);
        batchHistoryList.add(batchHistory2);

        batchHistoryList.forEach(batchHistory -> archiveDao.postBatchHistory(batchHistory));

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory latestBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyBatchHistory(latestBatchHistory, Status.COMPLETED, 0);
        // Should be nr 1 because it's the latest
        Assertions.assertEquals(batchHistory1.getEnd().plusDays(1), latestBatchHistory.getStart());
        Assertions.assertEquals(yesterday, latestBatchHistory.getEnd());
    }

    // Run batch and simulate request to Archive failure. Verify we handle exception correctly and continue with the rest.
    @Test
    void testErrorFromArchive() throws ServiceException, ApplicationException {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        Mockito.doThrow(ServiceException.create(POST_ARCHIVE_EXCEPTION_MESSAGE, null, Response.Status.INTERNAL_SERVER_ERROR)).when(archiveServiceMock).postArchive(any());

        // Test
        BatchHistory batchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        Assertions.assertEquals(Status.NOT_COMPLETED, archiveDao.getBatchHistory(batchHistory.getId()).getStatus());
        List<ArchiveHistory> archiveHistoryList = archiveDao.getArchiveHistories(batchHistory.getId());
        Assertions.assertEquals(3, archiveHistoryList.size());
        archiveHistoryList.forEach(archiveHistory -> Assertions.assertEquals(Status.NOT_COMPLETED, archiveHistory.getStatus()));

        verifyCalls(2, 3, 3, 0);
    }

    // Rerun an earlier not_completed batch - GET batchhistory and verify it was completed
    @Test
    void testReRunNotCompletedBatch() throws ServiceException, ApplicationException {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.GEO, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Mock
        Handling handling = arende1.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling().get(0);

        Attachment attachment = new Attachment();
        attachment.setExtension("." + handling.getDokument().getFil().getFilAndelse().toLowerCase());
        attachment.setName(handling.getDokument().getNamn() + "." + handling.getDokument().getFil().getFilAndelse());
        attachment.setFile(caseUtil.byteArrayToBase64(handling.getDokument().getFil().getFilBuffer()));

        ByggRArchiveRequest archiveMessage = new ByggRArchiveRequest();
        archiveMessage.setAttachment(attachment);
        Mockito.doThrow(ServiceException.create(POST_ARCHIVE_EXCEPTION_MESSAGE, null, Response.Status.INTERNAL_SERVER_ERROR)).when(archiveServiceMock).postArchive(Mockito.argThat(new ArchiveMessageAttachmentMatcher(archiveMessage)));

        // First run, fails
        BatchHistory firstBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        Assertions.assertEquals(1, archiveDao.getBatchHistories().size());
        Assertions.assertEquals(Status.NOT_COMPLETED, archiveDao.getBatchHistory(firstBatchHistory.getId()).getStatus());
        List<ArchiveHistory> firstArchiveHistoryList = archiveDao.getArchiveHistories(firstBatchHistory.getId());
        Assertions.assertEquals(3, firstArchiveHistoryList.size());
        List<ArchiveHistory> firstNotCompletedArchiveHistories = firstArchiveHistoryList.stream().filter(archiveHistory -> Status.NOT_COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        List<ArchiveHistory> firstCompletedArchiveHistories = firstArchiveHistoryList.stream().filter(archiveHistory -> Status.COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        Assertions.assertEquals(3, firstNotCompletedArchiveHistories.size());
        Assertions.assertEquals(0, firstCompletedArchiveHistories.size());

        verifyCalls(2, 3, 3, 0);

        // ReRun, success
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 111-111-111");
        Mockito.doReturn(archiveResponse).when(archiveServiceMock).postArchive(any());

        BatchHistory reRunBatchHistory = archiver.reRunBatch(firstBatchHistory.getId());

        Assertions.assertEquals(firstBatchHistory.getId(), reRunBatchHistory.getId());
        Assertions.assertEquals(Status.COMPLETED, reRunBatchHistory.getStatus());
        Assertions.assertEquals(1, archiveDao.getBatchHistories().size());
        List<ArchiveHistory> reRunArchiveHistoryList = archiveDao.getArchiveHistories(reRunBatchHistory.getId());
        Assertions.assertEquals(3, reRunArchiveHistoryList.size());
        // Now all should be COMPLETED
        List<ArchiveHistory> reRunNotCompletedArchiveHistories = reRunArchiveHistoryList.stream().filter(archiveHistory -> Status.NOT_COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        List<ArchiveHistory> reRunCompletedArchiveHistories = reRunArchiveHistoryList.stream().filter(archiveHistory -> Status.COMPLETED.equals(archiveHistory.getStatus())).collect(Collectors.toList());
        Assertions.assertEquals(0, reRunNotCompletedArchiveHistories.size());
        Assertions.assertEquals(3, reRunCompletedArchiveHistories.size());

        // Both the first batch and the reRun
        verifyCalls(4, 6, 6, 1);
    }

    // Run batch for attachmentCategory "GEO" and verify email was sent
    @Test
    void runBatchGeotekniskUndersokningMessageSentTrue() throws ServiceException, ApplicationException {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.GEO, AttachmentCategory.FASSIT2, AttachmentCategory.GEO));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("12312-3123-123-123-123");
        messageStatusResponse.setSent(true);
        Mockito.doReturn(messageStatusResponse).when(messagingServiceMock).postEmail(any());

        // Test

        BatchHistory batchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyBatchHistory(batchHistory, Status.COMPLETED, 3);
        archiveDao.getArchiveHistories(batchHistory.getId()).forEach(archiveHistory -> Assertions.assertEquals(Status.COMPLETED, archiveHistory.getStatus()));

        verifyCalls(2, 3, 3, 2);
    }

    // Run batch for attachmentCategory "GEO" and simulate the email was not sent. Verify we log the error and persist all.
    @Test
    void runBatchGeotekniskUndersokningMessageSentFalse() throws ServiceException, ApplicationException {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.GEO, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("12312-3123-123-123-123");
        messageStatusResponse.setSent(false);
        Mockito.doReturn(messageStatusResponse).when(messagingServiceMock).postEmail(any());

        // Test
        BatchHistory batchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyBatchHistory(batchHistory, Status.NOT_COMPLETED, 3);
        List<ArchiveHistory> archiveHistoryList = archiveDao.getArchiveHistories(batchHistory.getId());
        Assertions.assertEquals(1, archiveHistoryList.stream().filter(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED)).count());
        Assertions.assertEquals(2, archiveHistoryList.stream().filter(archiveHistory -> archiveHistory.getStatus().equals(Status.COMPLETED)).count());

        verifyCalls(2, 3, 3, 1);

        // TODO verify error message in the log
    }

    // Run batch for attachmentCategory "GEO" and simulate error when sending email. Rerun later with successful result.
    @Test
    void runBatchGeotekniskUndersokningErrorThenReRun() throws ServiceException, ApplicationException {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.GEO, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // mocks messaging
        Mockito.doThrow(ServiceException.create(POST_ARCHIVE_EXCEPTION_MESSAGE, null, Response.Status.INTERNAL_SERVER_ERROR)).when(messagingServiceMock).postEmail(any());

        // Test
        BatchHistory batchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        List<ArchiveHistory> archiveHistoryList = archiveDao.getArchiveHistories(batchHistory.getId());
        Assertions.assertEquals(1, archiveHistoryList.stream().filter(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED)).count());
        Assertions.assertEquals(2, archiveHistoryList.stream().filter(archiveHistory -> archiveHistory.getStatus().equals(Status.COMPLETED)).count());
        verifyBatchHistory(batchHistory, Status.NOT_COMPLETED, 3);
        verifyCalls(2, 3, 3, 1);

        // Rerun
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("b9535bce-fed9-4a42-a8b7-6fb6540aa3f3");
        messageStatusResponse.setSent(true);
        Mockito.doReturn(messageStatusResponse).when(messagingServiceMock).postEmail(any());

        BatchHistory reRunBatchHistory = archiver.reRunBatch(batchHistory.getId());
        List<ArchiveHistory> reRunArchiveHistoryList = archiveDao.getArchiveHistories(reRunBatchHistory.getId());
        Assertions.assertEquals(0, reRunArchiveHistoryList.stream().filter(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED)).count());
        Assertions.assertEquals(3, reRunArchiveHistoryList.stream().filter(archiveHistory -> archiveHistory.getStatus().equals(Status.COMPLETED)).count());
        // The batchHistory should now be completed
        verifyBatchHistory(reRunBatchHistory, Status.COMPLETED, 3);
        // The previous batch + this one with 2, 1, 0, 1
        verifyCalls(4, 4, 3, 2);

        // TODO verify error message in the log
    }

    @Test
    void testBilagaNamn() throws ApplicationException, ServiceException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        List<HandelseHandling> handelseHandlingList = arende1.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling();
        handelseHandlingList.get(0).getDokument().setNamn("test.without.extension");
        handelseHandlingList.get(0).getDokument().getFil().setFilAndelse(".docx");
        handelseHandlingList.get(1).getDokument().setNamn("test.without extension 2");
        handelseHandlingList.get(1).getDokument().getFil().setFilAndelse("pdf");
        handelseHandlingList.get(2).getDokument().setNamn("test.with   .extension.DOCX");

        arrayOfArende.getArende().addAll(List.of(arende1));
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationService).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = archiver.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        ArgumentCaptor<ByggRArchiveRequest> byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);
        verify(archiveServiceMock, times(3)).postArchive(byggRArchiveRequestArgumentCaptor.capture());

        byggRArchiveRequestArgumentCaptor.getAllValues().forEach(byggRArchiveRequest -> {
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("Bilaga Namn=\"test.without.extension.docx\" Lank=\"Bilagor\\test.without.extension.docx\"") ||
                    byggRArchiveRequest.getMetadata().contains("Bilaga Namn=\"test.without extension 2.pdf\" Lank=\"Bilagor\\test.without extension 2.pdf\"") ||
                    byggRArchiveRequest.getMetadata().contains("Bilaga Namn=\"test.with   .extension.DOCX\" Lank=\"Bilagor\\test.with   .extension.DOCX\""));
        });
        verifyCalls(3, 3, 3, 0);

        verifyBatchHistory(returnedBatchHistory, Status.COMPLETED, 3);
    }

    private void verifyBatchHistory(BatchHistory returnedBatchHistory, Status status, int expectedNumberOfRelatedArchiveHistories) {
        Assertions.assertEquals(status, archiveDao.getBatchHistory(returnedBatchHistory.getId()).getStatus());

        List<ArchiveHistory> relatedArchiveHistories = archiveDao.getArchiveHistories(returnedBatchHistory.getId());
        if (Status.COMPLETED.equals(status)) {
            relatedArchiveHistories.forEach(archiveHistory -> Assertions.assertEquals(Status.COMPLETED, archiveHistory.getStatus()));
        }

        System.out.println(archiveDao.getBatchHistories());
        Assertions.assertTrue(archiveDao.getBatchHistories().contains(returnedBatchHistory));
        Assertions.assertEquals(expectedNumberOfRelatedArchiveHistories, relatedArchiveHistories.size());
    }

    private void verifyCalls(int nrOfCallsToUpdatedArenden, int nrOfCallsToGetDocument, int nrOfCallsToPostArchive, int nrOfCallsToPostEmail) throws ServiceException {
        verify(arendeExportIntegrationService, times(nrOfCallsToUpdatedArenden)).getUpdatedArenden(any());
        verify(arendeExportIntegrationService, times(nrOfCallsToGetDocument)).getDocument(any());
        verify(archiveServiceMock, times(nrOfCallsToPostArchive)).postArchive(any());
        verify(messagingServiceMock, times(nrOfCallsToPostEmail)).postEmail(any());
    }
}
