package se.sundsvall.byggrarchiver.service;

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
import generated.se.sundsvall.bygglov.FastighetTyp;
import org.hibernate.service.spi.ServiceException;
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
import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.configuration.LongTermArchiveProperties;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.testutils.BatchFilterMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.ANS;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FASSIT2;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.GEO;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.LUTE;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.PLFASE;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.RUE;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.TOMTPLBE;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.SCHEDULED;
import static se.sundsvall.byggrarchiver.service.Constants.BYGGR_HANDELSETYP_ARKIV;
import static se.sundsvall.byggrarchiver.service.Constants.BYGGR_STATUS_AVSLUTAT;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

@ExtendWith(MockitoExtension.class)
class ArchiveHistoryServiceTest {

    public static final String ONGOING = "Pågående";

    @Mock
    private MessagingIntegration mockMessagingIntegration;
    @Mock
    private ArchiveHistoryRepository mockArchiveHistoryRepository;
    @Mock
    private BatchHistoryRepository mockBatchHistoryRepository;
    @Mock
    private FastighetService mockFastighetService;
    @Mock
    private ArendeExportIntegrationService mockArendeExportIntegrationService;
    @Mock
    private LongTermArchiveProperties mockLongTermArchiveProperties;

    @Mock ArchiveAttachmentService mockArchiveAttachmentService;

    @InjectMocks
    private ArchiveHistoryService archiveHistoryService;

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

        // FB
        final var fastighetTyp = new FastighetTyp();
        fastighetTyp.setFastighetsbeteckning("Sundsvall Test beteckning 1");
        fastighetTyp.setTrakt("Test trakt");
        fastighetTyp.setObjektidentitet(UUID.randomUUID().toString());

        lenient()
            .when(mockFastighetService.getFastighet(any()))
                .thenReturn(fastighetTyp);

        // Long-term archive
        lenient()
            .when(mockLongTermArchiveProperties.url())
                .thenReturn("someUrl");
    }

    // Standard scenario - Run batch for yesterday - 0 cases and documents found
    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch0Cases0Docs(BatchTrigger batchTrigger) throws Exception {
        var yesterday = LocalDate.now().minusDays(1);

        var result = archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        assertThat(result.getStart()).isEqualTo(yesterday);
        assertThat(result.getEnd()).isEqualTo(yesterday);
        assertThat(result.getBatchTrigger()).isEqualTo(batchTrigger);
        assertThat(result.getArchiveStatus()).isEqualTo(COMPLETED);

        verifyCalls(25, 0, 0, 0);
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

        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        verifyCalls(2, 0, 0, 0);
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

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);

        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        // Return empty document-list for one document
        doReturn(new ArrayList<>()).when(mockArendeExportIntegrationService).getDocument(arende.getHandelseLista().getHandelse().get(0).getHandlingLista().getHandling().get(1).getDokument().getDokId());


        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        verifyCalls(2, 3, 2, 0);
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

        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        verifyCalls(2, 0, 0, 0);
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

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);

        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        verifyCalls(2, 2, 2, 0);
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

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);

        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        verifyCalls(3, 3, 3, 0);
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

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);


        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        verifyCalls(2, 1, 1, 0);
    }

    @ParameterizedTest
    @EnumSource(BatchTrigger.class)
    void testBatch3Cases2Ended4Docs(BatchTrigger batchTrigger) throws Exception {
        final var yesterday = LocalDate.now().minusDays(1);

        final var start = yesterday.atStartOfDay();
        final var end = yesterday.atTime(23, 59, 59);

        final var batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(start);
        batchFilter.setUpperInclusiveBound(end);

        final var batchHistory = BatchHistory.builder()
            .withStart(yesterday)
            .withEnd(yesterday)
            .withBatchTrigger(batchTrigger)
            .withArchiveStatus(NOT_COMPLETED).build();

        final var arrayOfArende = new ArrayOfArende();
        final var arende1 = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(TOMTPLBE));
        final var arende2 = createArendeObject(ONGOING, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        final var arende3 = createArendeObject(BYGGR_STATUS_AVSLUTAT, BYGGR_HANDELSETYP_ARKIV, List.of(PLFASE, FASSIT2, TOMTPLBE));
        arrayOfArende.getArende().addAll(List.of(arende1, arende2, arende3));
        final var arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(start);
        arendeBatch.setBatchEnd(end);
        arendeBatch.setArenden(arrayOfArende);

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);
        archiveHistory.setBatchHistory(batchHistory);


        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);
        doReturn(arendeBatch).when(mockArendeExportIntegrationService).getUpdatedArenden(argThat(new BatchFilterMatcher(batchFilter)));

        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, batchTrigger));

        verifyCalls(2, 4, 4, 0);
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

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory_1);

        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, SCHEDULED));

        // verify deleteArchiveHistoriesByCaseIdAndArchiveStatus
        verify(mockArchiveHistoryRepository, times(2)).deleteArchiveHistoriesByCaseIdAndArchiveStatus(arende.getDnr(), NOT_COMPLETED);
        verify(mockBatchHistoryRepository, times(2)).save(batchHistoryCaptor.capture());

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

        doReturn(new ArrayList<>()).when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(any());

        var batch1 = BatchHistory.builder()
            .withArchiveStatus(NOT_COMPLETED)
            .withId(randomLong())
            .build();
        doReturn(List.of(batch1)).when(mockBatchHistoryRepository).findBatchHistoriesByArchiveStatus(NOT_COMPLETED);
        doReturn(new ArrayList<>()).when(mockArchiveHistoryRepository).getArchiveHistoriesByBatchHistoryId(batch1.getId());

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);
        archiveHistory.setBatchHistory(batch1);

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);

        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, SCHEDULED));

        // verify deleteArchiveHistoriesByCaseIdAndArchiveStatus
        verify(mockArchiveHistoryRepository, times(2)).deleteArchiveHistoriesByCaseIdAndArchiveStatus(arende.getDnr(), NOT_COMPLETED);
        verify(mockBatchHistoryRepository, times(2)).save(batchHistoryCaptor.capture());

        var batchHistory1 = batchHistoryCaptor.getAllValues().stream().filter(bh -> batch1.getId().equals(bh.getId())).findFirst().orElseThrow();
        assertThat(batchHistory1.getArchiveStatus()).isEqualTo(COMPLETED);
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

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);
        archiveHistory.setArchiveId(valueOf(randomLong()));

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);

        // Test
        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, SCHEDULED));

        verifyCalls(2, 3, 3, 2);
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

        final var archiveHistory = new ArchiveHistory();
        archiveHistory.setArchiveStatus(COMPLETED);
        archiveHistory.setArchiveId(valueOf(randomLong()));

        when(mockArchiveAttachmentService.archiveAttachment(any(), any(), any(), any()))
            .thenReturn(archiveHistory);

        // Test
        archiveHistoryService.archive(yesterday, yesterday, createBatchHistory(yesterday, yesterday, SCHEDULED));

        verifyCalls(2, 3, 3, 1);
    }

    private void verifyCalls(final int nrOfCallsToGetUpdatedArenden,
            final int nrOfCallsToGetDocument,
        final int nrOfCallsToArchiveAttachmentService,
        final int nrOfCallsToSendEmailToLantmateriet) throws ServiceException, ApplicationException {
        verify(mockArendeExportIntegrationService, times(nrOfCallsToGetUpdatedArenden)).getUpdatedArenden(any());
        verify(mockArendeExportIntegrationService, times(nrOfCallsToGetDocument)).getDocument(any());
        verify(mockArchiveAttachmentService, times(nrOfCallsToArchiveAttachmentService)).archiveAttachment(any(), any(), any(), any());

        verify(mockMessagingIntegration, times(nrOfCallsToSendEmailToLantmateriet))
            .sendEmailToLantmateriet(anyString(), any(ArchiveHistory.class));
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
            dokument.setDokId(valueOf(randomInt(999999)));
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

    private BatchHistory createBatchHistory(final LocalDate start, final LocalDate end, final BatchTrigger batchTrigger) {
        return BatchHistory.builder()
            .withStart(start)
            .withEnd(end)
            .withBatchTrigger(batchTrigger)
            .withArchiveStatus(NOT_COMPLETED).build();
    }
}
