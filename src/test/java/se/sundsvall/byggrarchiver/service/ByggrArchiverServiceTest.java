package se.sundsvall.byggrarchiver.service;

import arendeexport.Arende;
import arendeexport.ArendeBatch;
import arendeexport.ArendeFastighet;
import arendeexport.ArrayOfAbstractArendeObjekt2;
import arendeexport.ArrayOfArende;
import arendeexport.ArrayOfHandelse;
import arendeexport.ArrayOfHandelseHandling;
import arendeexport.BatchFilter;
import arendeexport.Dokument;
import arendeexport.DokumentFil;
import arendeexport.Fastighet;
import arendeexport.Handelse;
import arendeexport.HandelseHandling;
import arendeexport.Handling;
import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import generated.se.sundsvall.messaging.MessageStatusResponse;
import generated.sokigo.fb.FastighetDto;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.sundsvall.archive.ArchiveClient;
import se.sundsvall.byggrarchiver.integration.sundsvall.messaging.MessagingClient;
import se.sundsvall.byggrarchiver.service.util.Constants;
import se.sundsvall.byggrarchiver.service.util.Util;
import se.sundsvall.byggrarchiver.testutils.ArchiveMessageAttachmentMatcher;
import se.sundsvall.byggrarchiver.testutils.BatchFilterMatcher;
import se.sundsvall.dept44.exception.ServerProblem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ByggrArchiverServiceTest {

    public static final String POST_ARCHIVE_EXCEPTION_MESSAGE = """
            {
              "httpCode": 500,
              "message": "Service error",
              "technicalDetails": {
                "rootCode": 500,
                "rootCause": "Internal Server Error",
                "serviceId": "api-service-archive",
                "requestId": null,
                "details": [
                  "Error invoking subclass method",
                  "Request: /documents"
                ]
              }
            }""";
    public static final String ONGOING = "Pågående";

    @Mock
    private ArchiveClient archiveClientMock;
    @Mock
    private MessagingClient messagingClientMock;
    @Mock
    private ArchiveHistoryRepository archiveHistoryRepositoryMock;
    @Mock
    private BatchHistoryRepository batchHistoryRepositoryMock;
    @Mock
    private FbService fbServiceMock;
    @Mock
    private ArendeExportIntegrationService arendeExportIntegrationServiceMock;
    @Mock
    private Util utilMock;
    @InjectMocks
    private ByggrArchiverService byggrArchiverService;


    @BeforeEach
    void beforeEach() throws Exception {
        // ArendeExport
        ArendeBatch arendeBatch = new ArendeBatch();
        ArrayOfArende arrayOfArende = new ArrayOfArende();
        arendeBatch.setArenden(arrayOfArende);
        lenient().doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(any());

        // Messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("b9535bce-fed9-4a42-a8b7-6fb6540aa3f3");
        messageStatusResponse.setSent(true);
        lenient().doReturn(messageStatusResponse).when(messagingClientMock).postEmail(any());

        // Archiver
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 123-123-123");
        lenient().doReturn(archiveResponse).when(archiveClientMock).postArchive(any());

        // FB
        FastighetDto fastighetDto = new FastighetDto();
        fastighetDto.setKommun("Sundsvall");
        fastighetDto.setBeteckning("Test beteckning 1");
        fastighetDto.setTrakt("Test trakt");
        fastighetDto.setUuid(UUID.randomUUID());
        lenient().doReturn(fastighetDto).when(fbServiceMock).getPropertyInfoByFnr(any());

        // Util
        lenient().when(utilMock.getStringOrEmpty(anyString())).thenAnswer(i -> i.getArguments()[0]);
    }

    // Standard scenario - Run batch for yesterday - 0 cases and documents found
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch0Cases0Docs(BatchTrigger batchTrigger) throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        var result = byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);
        assertEquals(yesterday, result.getStart());
        assertEquals(yesterday, result.getEnd());
        assertEquals(batchTrigger, result.getBatchTrigger());
        assertEquals(ArchiveStatus.COMPLETED, result.getArchiveStatus());

        verifyCalls(25, 0, 0, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Cases0Docs(BatchTrigger batchTrigger) throws Exception {
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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 0, 0, 0);
    }

    // GetDocument returns empty list for one of the documents
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Cases3DocsGetDocumentReturnsEmpty(BatchTrigger batchTrigger) throws Exception {
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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Return empty document-list for one document
        Mockito.doReturn(new ArrayList<>()).when(arendeExportIntegrationServiceMock).getDocument(arende1.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling().get(1).getDokument().getDokId());

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 3, 2, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1CaseWithWrongStatus3Docs(BatchTrigger batchTrigger) throws Exception {
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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 0, 0, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch2Cases1WithWrongHandelseslag2Docs(BatchTrigger batchTrigger) throws Exception {
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

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 2, 2, 0);
    }


    // Standard scenario - Run batch for yesterday - 1 case and 3 documents found
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Case3Docs(BatchTrigger batchTrigger) throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(3, 3, 3, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch3Cases1Ended1Doc(BatchTrigger batchTrigger) throws Exception {
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

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 1, 1, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch3Cases2Ended4Docs(BatchTrigger batchTrigger) throws Exception {
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

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 4, 4, 0);
    }


    // Try to run scheduled batch for the same date and verify it doesn't run
    @Test
    void testRunScheduledBatchForSameDate() throws Exception {
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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Run the first batch
        BatchHistory firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        doReturn(List.of(firstBatchHistory)).when(batchHistoryRepositoryMock).findAll();

        // Run second batch with the same date
        BatchHistory secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);
        Assertions.assertNull(secondBatchHistory);

        // Only the first batch
        verifyCalls(2, 3, 3, 0);
    }

    // Try to run manual batch for the same date and verify it runs
    @Test
    void testRunManualBatchForSameDate() throws Exception {
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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Run the first batch
        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        doReturn(ArchiveHistory.builder().archiveStatus(ArchiveStatus.COMPLETED).build())
                .when(archiveHistoryRepositoryMock).getArchiveHistoryByDocumentIdAndCaseId(any(), any());
        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.MANUAL);

        // Both first and second batch
        verifyCalls(4, 3, 3, 0);
    }

    // Try to run batch for a date back in time and verify the scheduled batch change the startDate back in time to the day after latest scheduled batch.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapScheduled(BatchTrigger batchTrigger) throws Exception {
        LocalDate aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        BatchHistory firstBatchHistory = byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);

        doReturn(List.of(firstBatchHistory)).when(batchHistoryRepositoryMock).findAll();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        assertEquals(aLongTimeAgo.plusDays(1), secondBatchHistory.getStart());
        assertEquals(yesterday, secondBatchHistory.getEnd());

    }

    // Try to run batch for a date back in time and verify the manual batch does NOT change the startDate back in time.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapManual(BatchTrigger batchTrigger) throws Exception {
        LocalDate aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);

        LocalDate yesterday = LocalDate.now().minusDays(1);
        BatchHistory secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.MANUAL);

        assertEquals(yesterday, secondBatchHistory.getStart());
        assertEquals(yesterday, secondBatchHistory.getEnd());
    }

    // Run batch and simulate request to Archive failure.
    @Test
    void testErrorFromArchive() throws Exception {

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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));
        Mockito.doThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR, POST_ARCHIVE_EXCEPTION_MESSAGE)).when(archiveClientMock).postArchive(any());
        doReturn(List.of(ArchiveHistory.builder().archiveStatus(ArchiveStatus.NOT_COMPLETED).build())).when(archiveHistoryRepositoryMock).getArchiveHistoriesByBatchHistoryId(any());

        // Test
        var result = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);
        assertEquals(yesterday, result.getStart());
        assertEquals(yesterday, result.getEnd());
        assertEquals(BatchTrigger.SCHEDULED, result.getBatchTrigger());
        assertEquals(ArchiveStatus.NOT_COMPLETED, result.getArchiveStatus());

        // The first attempt + 3 retries
        verifyCalls(2, 3, 3, 0);
    }

    // Run batch and simulate request to Fb failure.
    @Test
    void testErrorFromFb() throws Exception {

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
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE));
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        Mockito.doThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR)).when(fbServiceMock).getPropertyInfoByFnr(any());

        // Test
        assertThrows(ThrowableProblem.class, () -> byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED));

        // The first attempt
        verifyCalls(1, 1, 0, 0);
    }

    // Rerun an earlier not_completed batch - GET batchhistory and verify it was completed
    @Test
    void testReRunNotCompletedBatch() throws Exception {

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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // Mock
        Handling handling = arende1.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling().get(0);

        Attachment attachment = new Attachment();
        attachment.setExtension("." + handling.getDokument().getFil().getFilAndelse().toLowerCase());
        attachment.setName(handling.getDokument().getNamn() + "." + handling.getDokument().getFil().getFilAndelse());

        ByggRArchiveRequest archiveMessage = new ByggRArchiveRequest();
        archiveMessage.setAttachment(attachment);
        Mockito.doThrow(ServerProblem.class).when(archiveClientMock).postArchive(Mockito.argThat(new ArchiveMessageAttachmentMatcher(archiveMessage)));
        doReturn(List.of(ArchiveHistory.builder()
                .archiveStatus(ArchiveStatus.NOT_COMPLETED)
                .build())).when(archiveHistoryRepositoryMock).getArchiveHistoriesByBatchHistoryId(any());

        // First run, fails
        var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);
        assertEquals(ArchiveStatus.NOT_COMPLETED, firstBatchHistory.getArchiveStatus());

        // The first attempt
        verifyCalls(2, 3, 3, 0);

        // ReRun, success
        ArchiveResponse archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 111-111-111");
        Mockito.doReturn(archiveResponse).when(archiveClientMock).postArchive(any());
        doReturn(Optional.of(firstBatchHistory)).when(batchHistoryRepositoryMock).findById(firstBatchHistory.getId());
        doReturn(List.of(ArchiveHistory.builder()
                .archiveStatus(ArchiveStatus.COMPLETED)
                .build())).when(archiveHistoryRepositoryMock).getArchiveHistoriesByBatchHistoryId(any());
        BatchHistory reRunBatchHistory = byggrArchiverService.reRunBatch(firstBatchHistory.getId());

        assertEquals(firstBatchHistory.getId(), reRunBatchHistory.getId());

        // Both the first batch and the reRun
        verifyCalls(4, 6, 6, 1);
    }

    @Test
    void rerunBatchThatDoesNotExist() {
        Long randomId = new Random().nextLong();

        var problem = assertThrows(ThrowableProblem.class, () -> byggrArchiverService.reRunBatch(randomId));

        assertEquals(Status.NOT_FOUND, problem.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"Message\":\"File format \\\".xlsx\\\" is not allowed for specified submission agreement.\"}",
            """
                    {
                      "type" : "https://zalando.github.io/problem/constraint-violation",
                      "status" : 400,
                      "violations" : [ {
                        "field" : "attachment.extension",
                        "message" : "extension must be valid. Must match rege
                    x: ^\\\\.(bmp|gif|tif|tiff|jpeg|jpg|png|htm|html|pdf|rtf|doc|docx|txt|xls|xlsx|odt|ods|pptx|ppt|msg)$"
                      } ],
                      "title" : "Constraint Violation"
                    }"""})
    void runBatchArchiveErrorExtension(String responseBody) throws Exception {

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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        Mockito.doThrow(Problem.valueOf(Status.BAD_REQUEST, responseBody)).when(archiveClientMock).postArchive(any());

        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("12312-3123-123-123-123");
        messageStatusResponse.setSent(true);
        Mockito.doReturn(messageStatusResponse).when(messagingClientMock).postEmail(any());

        // Test
        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyCalls(2, 3, 3, 3);
    }

    // Run batch for attachmentCategory "GEO" and verify email was sent
    @Test
    void runBatchGeotekniskUndersokningMessageSentTrue() throws Exception {

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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("12312-3123-123-123-123");
        messageStatusResponse.setSent(true);
        Mockito.doReturn(messageStatusResponse).when(messagingClientMock).postEmail(any());

        // Test
        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyCalls(2, 3, 3, 2);
    }

    // Run batch for attachmentCategory "GEO" and simulate the email was not sent.
    @Test
    void runBatchGeotekniskUndersokningMessageSentFalse() throws Exception {

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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // mocks messaging
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("12312-3123-123-123-123");
        messageStatusResponse.setSent(false);
        Mockito.doReturn(messageStatusResponse).when(messagingClientMock).postEmail(any());

        // Test
        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        verifyCalls(2, 3, 3, 1);
    }

    // Run batch for attachmentCategory "GEO" and simulate error when sending email. Rerun after and verify exception was thrown.
    @Test
    void runBatchGeotekniskUndersokningErrorThenReRun() throws Exception {

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
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        // mocks messaging
        Mockito.doThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR, POST_ARCHIVE_EXCEPTION_MESSAGE)).when(messagingClientMock).postEmail(any());

        // Test
        BatchHistory firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        // The batch should not fail just because we did not was able to send Lantmateriet info about Geoteknisk handling
        assertEquals(ArchiveStatus.COMPLETED, firstBatchHistory.getArchiveStatus());
        verifyCalls(2, 3, 3, 1);

        // Rerun
        MessageStatusResponse messageStatusResponse = new MessageStatusResponse();
        messageStatusResponse.setMessageId("b9535bce-fed9-4a42-a8b7-6fb6540aa3f3");
        messageStatusResponse.setSent(true);
        lenient().doReturn(messageStatusResponse).when(messagingClientMock).postEmail(any());
        doReturn(Optional.of(firstBatchHistory)).when(batchHistoryRepositoryMock).findById(firstBatchHistory.getId());

        Long batchHistoryId = firstBatchHistory.getId();
        var problem = assertThrows(DefaultProblem.class, () -> byggrArchiverService.reRunBatch(batchHistoryId));
        assertEquals(Status.BAD_REQUEST, problem.getStatus());
        assertEquals(Constants.IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH, problem.getDetail());

    }

    @Test
    void testBilagaNamn() throws Exception {
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

        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        ArgumentCaptor<ByggRArchiveRequest> byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);
        verify(archiveClientMock, times(3)).postArchive(byggRArchiveRequestArgumentCaptor.capture());

        byggRArchiveRequestArgumentCaptor.getAllValues().forEach(byggRArchiveRequest -> Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("Bilaga Namn=\"test.without.extension.docx\" Lank=\"Bilagor\\test.without.extension.docx\"") ||
                byggRArchiveRequest.getMetadata().contains("Bilaga Namn=\"test.without extension 2.pdf\" Lank=\"Bilagor\\test.without extension 2.pdf\"") ||
                byggRArchiveRequest.getMetadata().contains("Bilaga Namn=\"test.with   .extension.DOCX\" Lank=\"Bilagor\\test.with   .extension.DOCX\"")));
        verifyCalls(3, 3, 3, 0);

        assertEquals(ArchiveStatus.COMPLETED, returnedBatchHistory.getArchiveStatus());
    }

    @Test
    void testArkivbildareAnkomstNull() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));

        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        BatchHistory returnedBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        ArgumentCaptor<ByggRArchiveRequest> byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);
        verify(archiveClientMock, times(3)).postArchive(byggRArchiveRequestArgumentCaptor.capture());

        byggRArchiveRequestArgumentCaptor.getAllValues().forEach(byggRArchiveRequest -> {
            System.out.println(byggRArchiveRequest.getMetadata());
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.STADSBYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>2017</VerksamhetstidFran></Arkivbildare>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Klass>" + Constants.HANTERA_BYGGLOV + "</Klass>"));
        });
        verifyCalls(3, 3, 3, 0);

        assertEquals(ArchiveStatus.COMPLETED, returnedBatchHistory.getArchiveStatus());
    }

    @Test
    void testArkivbildareAnkomstEfter2017() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arende1.setAnkomstDatum(LocalDate.of(2017, 1, 1));
        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        ArgumentCaptor<ByggRArchiveRequest> byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);
        verify(archiveClientMock, times(3)).postArchive(byggRArchiveRequestArgumentCaptor.capture());

        byggRArchiveRequestArgumentCaptor.getAllValues().forEach(byggRArchiveRequest -> {
            System.out.println(byggRArchiveRequest.getMetadata());
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.STADSBYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>2017</VerksamhetstidFran></Arkivbildare>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Klass>" + Constants.HANTERA_BYGGLOV + "</Klass>"));
        });
        verifyCalls(3, 3, 3, 0);
    }

    @Test
    void testArkivbildareAnkomstInnan2017() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arende1.setAnkomstDatum(LocalDate.of(2016, 12, 1));

        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        ArgumentCaptor<ByggRArchiveRequest> byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);
        verify(archiveClientMock, times(3)).postArchive(byggRArchiveRequestArgumentCaptor.capture());

        byggRArchiveRequestArgumentCaptor.getAllValues().forEach(byggRArchiveRequest -> {
            System.out.println(byggRArchiveRequest.getMetadata());
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.STADSBYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>1993</VerksamhetstidFran><VerksamhetstidTill>2017</VerksamhetstidTill></Arkivbildare></Arkivbildare>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Klass>" + Constants.F_2_BYGGLOV + "</Klass>"));
        });
        verifyCalls(3, 3, 3, 0);
    }

    @Test
    void testArkivbildareAnkomstInnan1993() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));

        ArrayOfArende arrayOfArende = new ArrayOfArende();
        Arende arende1 = createArendeObject(Constants.BYGGR_STATUS_AVSLUTAT, Constants.BYGGR_HANDELSETYP_ARKIV, List.of(AttachmentCategory.PLFASE, AttachmentCategory.FASSIT2, AttachmentCategory.TOMTPLBE));
        arende1.setAnkomstDatum(LocalDate.of(1992, 12, 1));

        arrayOfArende.getArende().add(arende1);
        arendeBatch.setArenden(arrayOfArende);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(23, 59, 59);
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        Mockito.doReturn(arendeBatch).when(arendeExportIntegrationServiceMock).getUpdatedArenden(Mockito.argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, BatchTrigger.SCHEDULED);

        ArgumentCaptor<ByggRArchiveRequest> byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);
        verify(archiveClientMock, times(3)).postArchive(byggRArchiveRequestArgumentCaptor.capture());

        byggRArchiveRequestArgumentCaptor.getAllValues().forEach(byggRArchiveRequest -> {
            System.out.println(byggRArchiveRequest.getMetadata());
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Arkivbildare><Namn>" + Constants.BYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran><VerksamhetstidTill>1992</VerksamhetstidTill></Arkivbildare></Arkivbildare>"));
            Assertions.assertTrue(byggRArchiveRequest.getMetadata().contains("<Klass>" + Constants.F_2_BYGGLOV + "</Klass>"));
        });
        verifyCalls(3, 3, 3, 0);
    }

    private void verifyCalls(int nrOfCallsToGetUpdatedArenden, int nrOfCallsToGetDocument, int nrOfCallsToPostArchive, int nrOfCallsToPostEmail) throws ServiceException {
        verify(arendeExportIntegrationServiceMock, times(nrOfCallsToGetUpdatedArenden)).getUpdatedArenden(any());
        verify(arendeExportIntegrationServiceMock, times(nrOfCallsToGetDocument)).getDocument(any());
        verify(archiveClientMock, times(nrOfCallsToPostArchive)).postArchive(any());
        verify(messagingClientMock, times(nrOfCallsToPostEmail)).postEmail(any());
    }

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
        arende.setObjektLista(createArrayOfAbstractArendeObjekt());

        for (Dokument doc : dokumentList) {
            lenient().doReturn(List.of(doc)).when(arendeExportIntegrationServiceMock).getDocument(doc.getDokId());
        }

        return arende;

    }

    private ArrayOfAbstractArendeObjekt2 createArrayOfAbstractArendeObjekt() {
        ArendeFastighet arendeFastighet = new ArendeFastighet();
        Fastighet fastighet = new Fastighet();
        fastighet.setFnr(123456);
        arendeFastighet.setFastighet(fastighet);
        arendeFastighet.setArHuvudObjekt(true);
        ArrayOfAbstractArendeObjekt2 arrayOfAbstractArendeObjekt = new ArrayOfAbstractArendeObjekt2();
        arrayOfAbstractArendeObjekt.getAbstractArendeObjekt().add(arendeFastighet);
        return arrayOfAbstractArendeObjekt;
    }
}
