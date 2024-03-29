package se.sundsvall.byggrarchiver.service;

import generated.se.sundsvall.archive.ArchiveResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FASSIT2;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.GEO;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.TOMTPLBE;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.MANUAL;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.SCHEDULED;
import static se.sundsvall.byggrarchiver.service.Constants.BYGGR_HANDELSETYP_ARKIV;
import static se.sundsvall.byggrarchiver.service.Constants.BYGGR_STATUS_AVSLUTAT;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

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

    @Mock ArchiveHistoryService mockArchiveHistoryService;

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
    }
    // Try to run scheduled batch for the same date and verify it doesn't run
    @Test
    void testRunScheduledBatchForSameDate() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture())).thenReturn(BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(COMPLETED).build());

        // Run the first batch
        var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        doReturn(List.of(firstBatchHistory)).when(mockBatchHistoryRepository).findAll();

        // Run second batch with the same date
        var secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);
        assertThat(secondBatchHistory).isNull();

        // Only the first batch
        verify(mockArchiveHistoryService).archive(any(), any(), any());
    }

    @Test
    void testRunManualBatchForSameDate() throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture())).thenReturn(BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(COMPLETED).build());

        // Run the first batch
        var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        // Run second batch with the same date
        var secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, MANUAL);

        assertThat(secondBatchHistory).isEqualTo(firstBatchHistory);
        verify(mockArchiveHistoryService, times(2)).archive(any(), any(), any());
        verify(mockArchiveHistoryService, times(2)).archive(any(), any(), batchHistoryCaptor.capture());
    }

    // Try to run batch for a date back in time and verify the scheduled batch change the startDate back in time to the day after latest scheduled batch.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapScheduled(BatchTrigger batchTrigger) throws Exception {
        var aLongTimeAgo = LocalDate.now().minusDays(20);
        var yesterday = LocalDate.now().minusDays(1);

        when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture())).thenReturn(BatchHistory.builder().withStart(aLongTimeAgo).withEnd(aLongTimeAgo).withArchiveStatus(COMPLETED).build());

        // Run the first batch
        var firstBatchHistory = byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);

        doReturn(List.of(firstBatchHistory)).when(mockBatchHistoryRepository).findAll();

        byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);

        // First batch should have the same start and end date
        assertThat(batchHistoryCaptor.getAllValues().get(0).getStart()).isEqualTo(aLongTimeAgo);
        assertThat(batchHistoryCaptor.getAllValues().get(0).getEnd()).isEqualTo(aLongTimeAgo);
        // Second batch should have the start date set to the day after the first batch
        assertThat(batchHistoryCaptor.getAllValues().get(1).getStart()).isEqualTo(aLongTimeAgo.plusDays(1));
        assertThat(batchHistoryCaptor.getAllValues().get(1).getEnd()).isEqualTo(yesterday);
    }

    // Try to run batch for a date back in time and verify the manual batch does NOT change the startDate back in time.
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testTimeGapManual(BatchTrigger batchTrigger) throws Exception {
        var aLongTimeAgo = LocalDate.now().minusDays(20);

        when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture())).thenReturn(BatchHistory.builder().withStart(aLongTimeAgo).withEnd(aLongTimeAgo).withArchiveStatus(COMPLETED).build());

        // Run the first batch
        byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger);

        var yesterday = LocalDate.now().minusDays(1);
        byggrArchiverService.runBatch(yesterday, yesterday, MANUAL);

        // First batch should have the same start and end date
        assertThat(batchHistoryCaptor.getAllValues().get(0).getStart()).isEqualTo(aLongTimeAgo);
        assertThat(batchHistoryCaptor.getAllValues().get(0).getEnd()).isEqualTo(aLongTimeAgo);
        // Second batch should have the same start and end date
        assertThat(batchHistoryCaptor.getAllValues().get(1).getStart()).isEqualTo(yesterday);
        assertThat(batchHistoryCaptor.getAllValues().get(1).getEnd()).isEqualTo(yesterday);
    }

    @Test
    void testRunBatchScheduledWhenLatestBatchIsAfterCurrent() throws Exception {
        final var today = LocalDate.now();

        when(mockBatchHistoryRepository.findAll()).thenReturn(List.of(BatchHistory.builder().withStart(today.plusDays(1)).withEnd(today.plusDays(1)).withArchiveStatus(COMPLETED).build()));

        final var result = byggrArchiverService.runBatch(today, today, SCHEDULED);

        assertThat(result).isNull();
        verifyNoInteractions(mockArchiveHistoryService);
    }

    // Run batch and simulate request to Archive failure.
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

        // Mock
        when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture())).thenReturn(BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(NOT_COMPLETED).build())
            .thenReturn(BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(COMPLETED).build());

        // First run, fails
        var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED);
        assertThat(firstBatchHistory.getArchiveStatus()).isEqualTo(NOT_COMPLETED);

        // The first attempt
        verify(mockArchiveHistoryService).archive(any(), any(), any());

        // ReRun, success
        var archiveResponse = new ArchiveResponse();
        archiveResponse.setArchiveId("FORMPIPE ID 111-111-111");
        doReturn(Optional.of(firstBatchHistory)).when(mockBatchHistoryRepository).findById(firstBatchHistory.getId());

        var reRunBatchHistory = byggrArchiverService.reRunBatch(firstBatchHistory.getId());

        assertThat(reRunBatchHistory.getArchiveStatus()).isEqualTo(COMPLETED);
        assertEquals(firstBatchHistory.getId(), reRunBatchHistory.getId());

        // Both the first batch and the reRun
        verify(mockArchiveHistoryService, times(2)).archive(any(), any(), any());
        verify(mockArchiveHistoryService, times(2)).archive(any(), any(), batchHistoryCaptor.capture());
        assertThat(batchHistoryCaptor.getAllValues().get(0).getArchiveStatus()).isEqualTo(NOT_COMPLETED);
        assertThat(batchHistoryCaptor.getAllValues().get(1).getArchiveStatus()).isEqualTo(NOT_COMPLETED);
    }

    @Test
    void rerunBatch() throws Exception {
        final var randomId = randomLong();
        final var start = LocalDate.now().minusDays(7);
        final var end = LocalDate.now().minusDays(7);

        when(mockBatchHistoryRepository.findById(randomId))
            .thenReturn(Optional.of(BatchHistory.builder().withStart(start).withEnd(end).withId(randomId).withArchiveStatus(NOT_COMPLETED).build()));

        when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture()))
            .thenReturn(BatchHistory.builder().withStart(start).withEnd(end).withId(randomId).withArchiveStatus(COMPLETED).build());

        byggrArchiverService.reRunBatch(randomId);


        verify(mockArchiveHistoryService).archive(any(), any(), any());
        assertThat(batchHistoryCaptor.getValue().getStart()).isEqualTo(start);
        assertThat(batchHistoryCaptor.getValue().getEnd()).isEqualTo(end);
    }

    @Test
    void rerunBatchThatDoesNotExist() {
        var randomId = randomLong();

        assertThatExceptionOfType(ThrowableProblem.class)
            .isThrownBy(() -> byggrArchiverService.reRunBatch(randomId))
            .satisfies(throwableProblem -> assertThat(throwableProblem.getStatus()).isEqualTo(NOT_FOUND));
    }

    @Test
    void rerunBatchCompleted() throws Exception {
        final var randomId = randomLong();
        final var start = LocalDate.now().minusDays(7);
        final var end = LocalDate.now().minusDays(7);

        when(mockBatchHistoryRepository.findById(randomId))
            .thenReturn(Optional.of(BatchHistory.builder().withStart(start).withEnd(end).withId(randomId).withArchiveStatus(COMPLETED).build()));

        final var exception = assertThrows(ThrowableProblem.class, () -> byggrArchiverService.reRunBatch(randomId));

        assertThat(exception.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo("Bad Request: It's not possible to rerun a completed batch.");
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
