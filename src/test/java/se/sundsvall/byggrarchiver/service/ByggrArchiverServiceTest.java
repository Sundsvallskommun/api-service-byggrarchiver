package se.sundsvall.byggrarchiver.service;

import static feign.Request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.ANS;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FASSIT2;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.GEO;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.LUTE;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.PLFASE;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.RUE;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.TOMTPLBE;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.MANUAL;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.SCHEDULED;
import static se.sundsvall.byggrarchiver.service.ByggrArchiverService.BYGGNADSNAMNDEN;
import static se.sundsvall.byggrarchiver.service.ByggrArchiverService.BYGGR_HANDELSETYP_ARKIV;
import static se.sundsvall.byggrarchiver.service.ByggrArchiverService.BYGGR_STATUS_AVSLUTAT;
import static se.sundsvall.byggrarchiver.service.ByggrArchiverService.F_2_BYGGLOV;
import static se.sundsvall.byggrarchiver.service.ByggrArchiverService.HANTERA_BYGGLOV;
import static se.sundsvall.byggrarchiver.service.ByggrArchiverService.STADSBYGGNADSNAMNDEN;
import static se.sundsvall.byggrarchiver.service.ByggrArchiverService.SUNDSVALLS_KOMMUN;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.configuration.LongTermArchiveProperties;
import se.sundsvall.byggrarchiver.integration.archive.ArchiveIntegration;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.byggrarchiver.testutils.ArchiveMessageAttachmentMatcher;
import se.sundsvall.byggrarchiver.testutils.BatchFilterMatcher;

import feign.FeignException;
import feign.Request;
import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import generated.se.sundsvall.arendeexport.Arende;
import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.ArrayOfAbstractArendeObjekt2;
import generated.se.sundsvall.arendeexport.ArrayOfArende;
import generated.se.sundsvall.arendeexport.ArrayOfHandelse;
import generated.se.sundsvall.arendeexport.ArrayOfHandelseHandling;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.DokumentFil;
import generated.se.sundsvall.arendeexport.Fastighet;
import generated.se.sundsvall.arendeexport.Handelse;
import generated.se.sundsvall.arendeexport.HandelseHandling;
import generated.sokigo.fb.FastighetDto;

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
    private ArchiveIntegration mockArchiveIntegration;
    @Mock
    private MessagingIntegration mockMessagingIntegration;
    @Mock
    private ArchiveHistoryRepository mockArchiveHistoryRepository;
    @Mock
    private BatchHistoryRepository mockBatchHistoryRepository;
    @Mock
    private FbIntegration mockFbIntegration;
    @Mock
    private ArendeExportIntegrationService mockArendeExportIntegrationService;
    @Mock
    private LongTermArchiveProperties mockLongTermArchiveProperties;

    @InjectMocks
    private ByggrArchiverService byggrArchiverService;

    @Captor
    private ArgumentCaptor<BatchHistory> batchHistoryCaptor;

    @BeforeEach
    void beforeEach() throws Exception {
        // ArendeExport
        lenient()
            .when(mockArendeExportIntegrationService.getUpdatedArenden(any(BatchFilter.class)))
                .thenReturn(new ArendeBatch().withArenden(new ArrayOfArende()));

        // Messaging
        lenient()
            .doNothing()
            .when(mockMessagingIntegration).sendEmailToLantmateriet(anyString(), any(ArchiveHistory.class));
        lenient()
            .doNothing()
            .when(mockMessagingIntegration).sendExtensionErrorEmail(any(ArchiveHistory.class));

        // Archiver
        lenient()
            .when(mockArchiveIntegration.archive(any(ByggRArchiveRequest.class)))
                .thenReturn(new ArchiveResponse().archiveId("FORMPIPE ID 123-123-123"));

        // FB
        lenient()
            .when(mockFbIntegration.getPropertyInfoByFnr(anyInt()))
                .thenReturn(new FastighetDto()
                    .uuid(UUID.randomUUID())
                    .kommun("Sundsvall")
                    .beteckning("Test beteckning 1")
                    .trakt("Test trakt"));

        // Long-term archive
        lenient()
            .when(mockLongTermArchiveProperties.url())
                .thenReturn("someUrl");

        // E-mail
        /*
        var instance = new EmailProperties.Instance("someSender", "someRecipient");
        lenient()
            .when(mockEmailProperties.geo())
                .thenReturn(instance);
        lenient()
            .when(mockEmailProperties.extensionError())
                .thenReturn(instance);*/
    }

    // Standard scenario - Run batch for yesterday - 0 cases and documents found
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch0Cases0Docs(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var result = byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        assertThat(result.getStart()).isEqualTo(yesterday);
        assertThat(result.getEnd()).isEqualTo(yesterday);
        assertThat(result.getBatchTrigger()).isEqualTo(batchTrigger);
        assertThat(result.getArchiveStatus()).isEqualTo(COMPLETED);

        verifyCalls(25, 0, 0, 0, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Cases0Docs(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of());
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);

        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);

        arendeBatch.setArenden(arrayOfArende);

        when(mockArendeExportIntegrationService.getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter))))
            .thenReturn(arendeBatch);

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 0, 0, 0, 0);
    }

    // GetDocument returns empty list for one of the documents
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Cases3DocsGetDocumentReturnsEmpty(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);

        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // Return empty document-list for one document
        doReturn(new ArrayList<>()).when(mockArendeExportIntegrationService).getDocument(arende.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling().get(1).getDokument().getDokId());

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 3, 2, 0, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1CaseWithWrongStatus3Docs(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arende = createArendeObject(ONGOING, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 0, 0, 0, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch2Cases1WithWrongHandelseslag2Docs(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arende1 = createArendeObject(BYGGR_STATUS_AVSLUTAT, "BESLUT", List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arende2 = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(LUTE, RUE));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().addAll(List.of(arende1, arende2));
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 2, 2, 0, 0);
    }

    // Standard scenario - Run batch for yesterday - 1 case and 3 documents found
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch1Case3Docs(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));
        arendeBatch.setArenden(arrayOfArende);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(yesterday.atStartOfDay());
        batchFilter.setUpperInclusiveBound(yesterday.atTime(23, 59, 59));

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(3, 3, 3, 0, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch3Cases1Ended1Doc(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arende1 = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(TOMTPLBE));
        var arende2 = createArendeObject(ONGOING, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arende3 = createArendeObject(ONGOING, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().addAll(List.of(arende1, arende2, arende3));
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 1, 1, 0, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch3Cases2Ended4Docs(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende1 = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(TOMTPLBE));
        var arende2 = createArendeObject(ONGOING, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arende3 = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1, arende2, arende3));
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, batchTrigger);

        verifyCalls(2, 4, 4, 0, 0);
    }

    // Try to run scheduled batch for the same date and verify it doesn't run
    @Test
    void testRunScheduledBatchForSameDate() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // Run the first batch
        var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        doReturn(List.of(firstBatchHistory)).when(mockBatchHistoryRepository).findAll();

        // Run second batch with the same date
        var secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);
        assertThat(secondBatchHistory).isNull();

        // Only the first batch
        verifyCalls(2, 3, 3, 0, 0);
    }

    // Try to run manual batch for the same date and verify it runs
    @Test
    void testRunManualBatchForSameDate() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // Run the first batch
        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        doReturn(Optional.of(ArchiveHistory.builder().withArchiveStatus(COMPLETED).build()))
            .when(mockArchiveHistoryRepository).getArchiveHistoryByDocumentIdAndCaseId(any(), any());

        byggrArchiverService.runBatch(yesterday, yesterday, MANUAL);

        // Both first and second batch
        verifyCalls(4, 3, 3, 0, 0);
    }

    // Try to run batch for a date back in time and verify the scheduled batch change the startDate back in time to the day after latest scheduled batch.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapScheduled(BatchTrigger batchTrigger) throws Exception {
        var aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        var firstBatchHistory = byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);

        doReturn(List.of(firstBatchHistory)).when(mockBatchHistoryRepository).findAll();

        var yesterday = LocalDate.now().minusDays(1);
        var secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        assertThat(secondBatchHistory.getStart()).isEqualTo(aLongTimeAgo.plusDays(1));
        assertThat(secondBatchHistory.getEnd()).isEqualTo(yesterday);
    }

    // Try to run batch for a date back in time and verify the manual batch does NOT change the startDate back in time.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapManual(BatchTrigger batchTrigger) throws Exception {
        var aLongTimeAgo = LocalDate.now().minusDays(20);

        // Run the first batch
        byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);

        var yesterday = LocalDate.now().minusDays(1);
        var secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, MANUAL);

        assertThat(secondBatchHistory.getStart()).isEqualTo(yesterday);
        assertThat(secondBatchHistory.getEnd()).isEqualTo(yesterday);
    }

    // Run batch and simulate request to Archive failure.
    @Test
    void testErrorFromArchive() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));
        doThrow(new FeignException.InternalServerError("Some test error", Request.create(POST, "url", Map.of(), null, null, null), null, null))
            .when(mockArchiveIntegration).archive(any());
        doReturn(List.of(ArchiveHistory.builder().withArchiveStatus(NOT_COMPLETED).build()))
            .when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(any());

        // Test
        var result = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);
        assertThat(result.getStart()).isEqualTo(yesterday);
        assertThat(result.getEnd()).isEqualTo(yesterday);
        assertThat(result.getBatchTrigger()).isEqualTo(SCHEDULED);
        assertThat(result.getArchiveStatus()).isEqualTo(NOT_COMPLETED);

        // The first attempt + 3 retries
        verifyCalls(2, 3, 3, 0, 0);
    }

    // Run batch and simulate request to Fb failure.
    @Test
    void testErrorFromFb() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));
        doThrow(Problem.valueOf(INTERNAL_SERVER_ERROR)).when(mockFbIntegration).getPropertyInfoByFnr(any());

        // Test
        assertThatExceptionOfType(ThrowableProblem.class)
            .isThrownBy(() -> byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED));

        // The first attempt
        verifyCalls(1, 1, 0, 0, 0);
    }

    // Rerun an earlier not_completed batch - GET batchhistory and verify it was completed
    @Test
    void testReRunNotCompletedBatch() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(GEO, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // Mock
        var handling = arende.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling().get(0);

        var attachment = new Attachment();
        attachment.setExtension("." + handling.getDokument().getFil().getFilAndelse().toLowerCase());
        attachment.setName(handling.getDokument().getNamn() + "." + handling.getDokument().getFil().getFilAndelse());

        var archiveMessage = new ByggRArchiveRequest();
        archiveMessage.setAttachment(attachment);
        doThrow(new FeignException.InternalServerError("Some test error", Request.create(POST, "url", Map.of(), null, null, null), null, null))
            .when(mockArchiveIntegration).archive(argThat(new ArchiveMessageAttachmentMatcher(archiveMessage)));
        doReturn(List.of(ArchiveHistory.builder()
                .withArchiveStatus(NOT_COMPLETED)
                .build()))
            .when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(any());

        // First run, fails
        var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);
        assertThat(firstBatchHistory.getArchiveStatus()).isEqualTo(NOT_COMPLETED);

        // The first attempt
        verifyCalls(2, 3, 3, 0, 0);

        // ReRun, success
        var archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 111-111-111");
        doReturn(archiveResponse).when(mockArchiveIntegration).archive(any());
        doReturn(Optional.of(firstBatchHistory)).when(mockBatchHistoryRepository).findById(firstBatchHistory.getId());
        doReturn(List.of(ArchiveHistory.builder()
                .withArchiveStatus(COMPLETED)
                .build()))
            .when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(any());
        var reRunBatchHistory = byggrArchiverService.reRunBatch(firstBatchHistory.getId());

        assertEquals(firstBatchHistory.getId(), reRunBatchHistory.getId());

        // Both the first batch and the reRun
        verifyCalls(4, 6, 6, 1, 0);
    }

    // Test run a batch with a case that has already been archived. Verify that every archive history that is not completed
    // and connected to this case is removed and that the old batch is updated with status completed.
    @Test
    void testUpdateStatusOfOldBatchHistories_1() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(ANS, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(any());
        doReturn(Optional.empty()).when(mockArchiveHistoryRepository).getArchiveHistoryByDocumentIdAndCaseId(any(), any());

        var archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 111-111-111");
        doReturn(archiveResponse).when(mockArchiveIntegration).archive(any());

        doReturn(List.of(ArchiveHistory.builder()
                .withArchiveStatus(COMPLETED)
                .build()))
            .when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(any());

        var batch1 = BatchHistory.builder()
            .withId(randomLong())
            .withArchiveStatus(NOT_COMPLETED)
            .build();
        doReturn(List.of(batch1)).when(mockBatchHistoryRepository).findBatchHistoriesByArchiveStatus(NOT_COMPLETED);

        var archiveHistory_1 = ArchiveHistory.builder()
            .withArchiveStatus(COMPLETED)
            .withBatchHistory(batch1)
            .build();

        var archiveHistory_2 = ArchiveHistory.builder()
            .withArchiveStatus(COMPLETED)
            .withBatchHistory(batch1)
            .build();

        doReturn(List.of(archiveHistory_1, archiveHistory_2)).when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(batch1.getId());

        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        // verify deleteArchiveHistoriesByCaseIdAndArchiveStatus
        verify(mockArchiveHistoryRepository, times(2)).deleteArchiveHistoriesByCaseIdAndArchiveStatus(arende.getDnr(), NOT_COMPLETED);
        verify(mockBatchHistoryRepository, times(3)).save(batchHistoryCaptor.capture());

        var batchHistory1 = batchHistoryCaptor.getAllValues().stream().filter(bh -> batch1.getId().equals(bh.getId())).findFirst().orElseThrow();
        assertThat(batchHistory1.getArchiveStatus()).isEqualTo(COMPLETED);
    }

    // Verify an empty list also works in updateStatusOfOldBatchHistories
    @Test
    void testUpdateStatusOfOldBatchHistories_2() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(ANS, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(any());
        doReturn(Optional.empty()).when(mockArchiveHistoryRepository).getArchiveHistoryByDocumentIdAndCaseId(any(), any());

        var archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 111-111-111");
        doReturn(archiveResponse).when(mockArchiveIntegration).archive(any());

        doReturn(new ArrayList<>()).when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(any());

        var batch1 = BatchHistory.builder()
            .withArchiveStatus(NOT_COMPLETED)
            .withId(randomLong())
            .build();
        doReturn(List.of(batch1)).when(mockBatchHistoryRepository).findBatchHistoriesByArchiveStatus(NOT_COMPLETED);
        doReturn(new ArrayList<>()).when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(batch1.getId());

        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        // verify deleteArchiveHistoriesByCaseIdAndArchiveStatus
        verify(mockArchiveHistoryRepository, times(2)).deleteArchiveHistoriesByCaseIdAndArchiveStatus(arende.getDnr(), NOT_COMPLETED);
        verify(mockBatchHistoryRepository, times(3)).save(batchHistoryCaptor.capture());

        var batchHistory1 = batchHistoryCaptor.getAllValues().stream().filter(bh -> batch1.getId().equals(bh.getId())).findFirst().orElseThrow();
        assertThat(batchHistory1.getArchiveStatus()).isEqualTo(COMPLETED);
    }

    @Test
    void rerunBatchThatDoesNotExist() {
        var randomId = randomLong();

        assertThatExceptionOfType(ThrowableProblem.class)
            .isThrownBy(() -> byggrArchiverService.reRunBatch(randomId))
            .satisfies(throwableProblem -> assertThat(throwableProblem.getStatus()).isEqualTo(NOT_FOUND));
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
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(GEO, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        doThrow(new FeignException.BadRequest(responseBody, Request.create(POST, "url", Map.of(), null, null, null), null, null)).when(mockArchiveIntegration).archive(any());

        // Test
        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        verifyCalls(2, 3, 3, 0, 3);
    }

    // Run batch for attachmentCategory "GEO" and verify email was sent
    @Test
    void runBatchGeotekniskUndersokningMessageSentTrue() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(GEO, FASSIT2, GEO));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // Test
        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        verifyCalls(2, 3, 3, 2, 0);
    }

    // Run batch for attachmentCategory "GEO" and simulate the email was not sent.
    @Test
    void runBatchGeotekniskUndersokningMessageSentFalse() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(GEO, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // mocks messaging
        doNothing().when(mockMessagingIntegration).sendEmailToLantmateriet(anyString(), any(ArchiveHistory.class));

        // Test
        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        verifyCalls(2, 3, 3, 1, 0);
    }

    // Run batch for attachmentCategory "GEO" and simulate error when sending email. Rerun after and verify exception was thrown.
    @Test
    void runBatchGeotekniskUndersokningErrorThenReRun() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        var arrayOfArende = new ArrayOfArende();
        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(GEO, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // Test
        var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        // The batch should not fail just because we did not was able to send Lantmateriet info about Geoteknisk handling
        assertThat(firstBatchHistory.getArchiveStatus()).isEqualTo(COMPLETED);

        verifyCalls(2, 3, 3, 1, 0); // TODO

        // Rerun
        doReturn(Optional.of(firstBatchHistory)).when(mockBatchHistoryRepository).findById(firstBatchHistory.getId());

        var batchHistoryId = firstBatchHistory.getId();

        assertThatExceptionOfType(DefaultProblem.class)
            .isThrownBy(() -> byggrArchiverService.reRunBatch(batchHistoryId))
            .satisfies(problem -> {
                assertThat(problem.getStatus()).isEqualTo(BAD_REQUEST);
                assertThat(problem.getDetail()).isEqualTo("It's not possible to rerun a completed batch.");
            });
    }

    @Test
    void testBilagaNamn() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var handelseHandlingList = arende.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling();
        handelseHandlingList.get(0).getDokument().setNamn("test.without.extension");
        handelseHandlingList.get(0).getDokument().getFil().setFilAndelse(".docx");
        handelseHandlingList.get(1).getDokument().setNamn("test.without extension 2");
        handelseHandlingList.get(1).getDokument().getFil().setFilAndelse("pdf");
        handelseHandlingList.get(2).getDokument().setNamn("test.with   .extension.DOCX");
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));
        arendeBatch.setArenden(arrayOfArende);

        var start = yesterday.atStartOfDay();
        var end = yesterday.atTime(23, 59, 59);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        var returnedBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        var byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);
        verify(mockArchiveIntegration, times(3)).archive(byggRArchiveRequestArgumentCaptor.capture());

        assertThat(byggRArchiveRequestArgumentCaptor.getAllValues()).allSatisfy(request ->
            assertThat(request.getMetadata()).containsAnyOf(
                "Bilaga Namn=\"test.without.extension.docx\" Lank=\"Bilagor\\test.without.extension.docx\"",
                "Bilaga Namn=\"test.without extension 2.pdf\" Lank=\"Bilagor\\test.without extension 2.pdf\"",
                "Bilaga Namn=\"test.with   .extension.DOCX\" Lank=\"Bilagor\\test.with   .extension.DOCX\""
            )
        );
        verifyCalls(3, 3, 3, 0, 0);

        assertThat(returnedBatchHistory.getArchiveStatus()).isEqualTo(COMPLETED);
    }

    @Test
    void testArkivbildareAnkomstNull() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));
        arendeBatch.setArenden(arrayOfArende);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(yesterday.atStartOfDay());
        batchFilter.setUpperInclusiveBound(yesterday.atTime(23, 59, 59));

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        var returnedBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        var byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);

        verify(mockArchiveIntegration, times(3)).archive(byggRArchiveRequestArgumentCaptor.capture());

        assertThat(byggRArchiveRequestArgumentCaptor.getAllValues()).allSatisfy(request -> {
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>");
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + STADSBYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>2017</VerksamhetstidFran></Arkivbildare>");
            assertThat(request.getMetadata()).contains("<Klass>" + HANTERA_BYGGLOV + "</Klass>");
        });

        verifyCalls(3, 3, 3, 0, 0);

        assertThat(returnedBatchHistory.getArchiveStatus()).isEqualTo(COMPLETED);
    }

    @Test
    void testArkivbildareAnkomstEfter2017() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arende.setAnkomstDatum(LocalDate.of(2017, 1, 1));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));
        arendeBatch.setArenden(arrayOfArende);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(yesterday.atStartOfDay());
        batchFilter.setUpperInclusiveBound(yesterday.atTime(23, 59, 59));

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        var byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);

        verify(mockArchiveIntegration, times(3)).archive(byggRArchiveRequestArgumentCaptor.capture());

        assertThat(byggRArchiveRequestArgumentCaptor.getAllValues()).allSatisfy(request -> {
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>");
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + STADSBYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>2017</VerksamhetstidFran></Arkivbildare>");
            assertThat(request.getMetadata()).contains("<Klass>" + HANTERA_BYGGLOV + "</Klass>");
        });

        verifyCalls(3, 3, 3, 0, 0);
    }

    @Test
    void testArkivbildareAnkomstInnan2017() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arende.setAnkomstDatum(LocalDate.of(2016, 12, 1));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));
        arendeBatch.setArenden(arrayOfArende);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(yesterday.atStartOfDay());
        batchFilter.setUpperInclusiveBound(yesterday.atTime(23, 59, 59));

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        var byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);

        verify(mockArchiveIntegration, times(3)).archive(byggRArchiveRequestArgumentCaptor.capture());

        assertThat(byggRArchiveRequestArgumentCaptor.getAllValues()).allSatisfy(request -> {
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>");
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + STADSBYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>1993</VerksamhetstidFran><VerksamhetstidTill>2017</VerksamhetstidTill></Arkivbildare></Arkivbildare>");
            assertThat(request.getMetadata()).contains("<Klass>" + F_2_BYGGLOV + "</Klass>");
        });

        verifyCalls(3, 3, 3, 0, 0);
    }

    @Test
    void testArkivbildareAnkomstInnan1993() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var arende = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arende.setAnkomstDatum(LocalDate.of(1992, 12, 1));
        var arrayOfArende = new ArrayOfArende();
        arrayOfArende.getArende().add(arende);
        var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(LocalDateTime.now().minusDays(1).withHour(12).withMinute(0).withSecond(0));
        arendeBatch.setBatchEnd(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0));
        arendeBatch.setArenden(arrayOfArende);

        var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(yesterday.atStartOfDay());
        batchFilter.setUpperInclusiveBound(yesterday.atTime(23, 59, 59));

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        var byggRArchiveRequestArgumentCaptor = ArgumentCaptor.forClass(ByggRArchiveRequest.class);

        verify(mockArchiveIntegration, times(3)).archive(byggRArchiveRequestArgumentCaptor.capture());

        assertThat(byggRArchiveRequestArgumentCaptor.getAllValues()).allSatisfy(request -> {
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + SUNDSVALLS_KOMMUN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran>");
            assertThat(request.getMetadata()).contains("<Arkivbildare><Namn>" + BYGGNADSNAMNDEN + "</Namn><VerksamhetstidFran>1974</VerksamhetstidFran><VerksamhetstidTill>1992</VerksamhetstidTill></Arkivbildare></Arkivbildare>");
            assertThat(request.getMetadata()).contains("<Klass>" + F_2_BYGGLOV + "</Klass>");
        });

        verifyCalls(3, 3, 3, 0, 0);
    }

    private void verifyCalls(final int nrOfCallsToGetUpdatedArenden,
            final int nrOfCallsToGetDocument, final int nrOfCallsToPostArchive,
            final int nrOfCallsToSendEmailToLantmateriet,
            final int nrOfCallsToSendExtensionErrorEmail) throws ServiceException {
        verify(mockArendeExportIntegrationService, times(nrOfCallsToGetUpdatedArenden)).getUpdatedArenden(any());
        verify(mockArendeExportIntegrationService, times(nrOfCallsToGetDocument)).getDocument(any());
        verify(mockArchiveIntegration, times(nrOfCallsToPostArchive)).archive(any());
        verify(mockMessagingIntegration, times(nrOfCallsToSendEmailToLantmateriet))
            .sendEmailToLantmateriet(anyString(), any(ArchiveHistory.class));
        verify(mockMessagingIntegration, times(nrOfCallsToSendExtensionErrorEmail))
            .sendExtensionErrorEmail(any(ArchiveHistory.class));
    }

    /**
     * Util method for creating arende-objects
     *
     * @param status               - status for the arende
     * @param handelsetyp          - type of handelse that should be included
     * @param attachmentCategories - the documents that should be generated
     * @return Arende
     */
    private Arende createArendeObject(final String status, final String handelsetyp,
            final List<AttachmentCategory> attachmentCategories) {
        var arrayOfHandelseHandling = new ArrayOfHandelseHandling();
        var dokumentList = new ArrayList<Dokument>();
        attachmentCategories.forEach(category -> {
            var dokument = new Dokument();
            dokument.setDokId(String.valueOf(randomInt(999999)));
            dokument.setNamn("Test filnamn");
            var dokumentFil = new DokumentFil();
            dokumentFil.setFilAndelse("pdf");
            dokument.setFil(dokumentFil);
            dokument.setSkapadDatum(LocalDateTime.now().minusDays(30));

            dokumentList.add(dokument);

            var handling = new HandelseHandling();
            handling.setTyp(category.name());
            handling.setDokument(dokument);

            arrayOfHandelseHandling.getHandling().add(handling);
        });

        var handelse = new Handelse();
        handelse.setHandelsetyp(handelsetyp);
        handelse.setHandlingLista(arrayOfHandelseHandling);
        var arrayOfHandelse = new ArrayOfHandelse();
        arrayOfHandelse.getHandelse().add(handelse);
        var arende = new Arende();
        arende.setDnr("BYGG 2021-" + randomInt(999999));
        arende.setStatus(status);
        arende.setHandelseLista(arrayOfHandelse);
        arende.setObjektLista(createArrayOfAbstractArendeObjekt());

        for (var doc : dokumentList) {
            lenient().doReturn(List.of(doc)).when(mockArendeExportIntegrationService).getDocument(doc.getDokId());
        }

        return arende;
    }

    private ArrayOfAbstractArendeObjekt2 createArrayOfAbstractArendeObjekt() {
        var fastighet = new Fastighet();
        fastighet.setFnr(123456);
        var arendeFastighet = new ArendeFastighet();
        arendeFastighet.setFastighet(fastighet);
        arendeFastighet.setArHuvudObjekt(true);
        var arrayOfAbstractArendeObjekt = new ArrayOfAbstractArendeObjekt2();
        arrayOfAbstractArendeObjekt.getAbstractArendeObjekt().add(arendeFastighet);
        return arrayOfAbstractArendeObjekt;
    }
}
