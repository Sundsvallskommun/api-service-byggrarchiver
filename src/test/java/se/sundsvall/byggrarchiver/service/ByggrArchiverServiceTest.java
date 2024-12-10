package se.sundsvall.byggrarchiver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportIntegration;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;

@ExtendWith(MockitoExtension.class)
class ByggrArchiverServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	ArchiveHistoryService mockArchiveHistoryService;

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
	private ArendeExportIntegration mockArendeExportIntegrationService;

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
			.when(mockMessagingIntegration).sendEmailToLantmateriet(anyString(), any(ArchiveHistory.class), any());
		lenient()
			.doNothing()
			.when(mockMessagingIntegration).sendExtensionErrorEmail(any(ArchiveHistory.class), any());

		// Archiver
		lenient()
			.when(mockArchiveIntegration.archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID)))
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
	void testRunScheduledBatchForSameDate() {
		final var yesterday = LocalDate.now().minusDays(1);
		final var batchHistory = BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(COMPLETED).build();

		when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(batchHistory);

		// Run the first batch
		byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED, MUNICIPALITY_ID);

		when(mockBatchHistoryRepository.findAll()).thenReturn(List.of(batchHistory));

		// Run second batch with the same date
		final var secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED, MUNICIPALITY_ID);
		assertThat(secondBatchHistory).isNull();

		// Only the first batch
		verify(mockArchiveHistoryService).archive(any(), any(), any(), eq(MUNICIPALITY_ID));
	}

	@Test
	void testRunManualBatchForSameDate() {
		final var yesterday = LocalDate.now().minusDays(1);

		when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(COMPLETED).build());

		// Run the first batch
		final var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED, MUNICIPALITY_ID);

		// Run second batch with the same date
		final var secondBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, MANUAL, MUNICIPALITY_ID);

		assertThat(secondBatchHistory).isEqualTo(firstBatchHistory);
		verify(mockArchiveHistoryService, times(2)).archive(any(), any(), any(), any());
		verify(mockArchiveHistoryService, times(2)).archive(any(), any(), batchHistoryCaptor.capture(), any());
	}

	// Try to run batch for a date back in time and verify the scheduled batch change the startDate back in time to the day
	// after latest scheduled batch.
	@ParameterizedTest
	@EnumSource(BatchTrigger.class)
	void testTimeGapScheduled(final BatchTrigger batchTrigger) {
		final var aLongTimeAgo = LocalDate.now().minusDays(20);
		final var yesterday = LocalDate.now().minusDays(1);
		final var batchHistory = BatchHistory.builder().withStart(aLongTimeAgo).withEnd(aLongTimeAgo).withArchiveStatus(COMPLETED).build();

		when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(batchHistory);

		// Run the first batch
		byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger, MUNICIPALITY_ID);

		when(mockBatchHistoryRepository.findAll()).thenReturn(List.of(batchHistory));

		byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED, MUNICIPALITY_ID);

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
	void testTimeGapManual(final BatchTrigger batchTrigger) {
		final var aLongTimeAgo = LocalDate.now().minusDays(20);

		when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(BatchHistory.builder().withStart(aLongTimeAgo).withEnd(aLongTimeAgo).withArchiveStatus(COMPLETED).build());

		// Run the first batch
		byggrArchiverService.runBatch(aLongTimeAgo, aLongTimeAgo, batchTrigger, MUNICIPALITY_ID);

		final var yesterday = LocalDate.now().minusDays(1);
		byggrArchiverService.runBatch(yesterday, yesterday, MANUAL, MUNICIPALITY_ID);

		// First batch should have the same start and end date
		assertThat(batchHistoryCaptor.getAllValues().get(0).getStart()).isEqualTo(aLongTimeAgo);
		assertThat(batchHistoryCaptor.getAllValues().get(0).getEnd()).isEqualTo(aLongTimeAgo);
		// Second batch should have the same start and end date
		assertThat(batchHistoryCaptor.getAllValues().get(1).getStart()).isEqualTo(yesterday);
		assertThat(batchHistoryCaptor.getAllValues().get(1).getEnd()).isEqualTo(yesterday);
	}

	@Test
	void testRunBatchScheduledWhenLatestBatchIsAfterCurrent() {
		final var today = LocalDate.now();

		when(mockBatchHistoryRepository.findAll()).thenReturn(List.of(BatchHistory.builder().withStart(today.plusDays(1)).withEnd(today.plusDays(1)).withArchiveStatus(COMPLETED).build()));

		final var result = byggrArchiverService.runBatch(today, today, SCHEDULED, MUNICIPALITY_ID);

		assertThat(result).isNull();
		verifyNoInteractions(mockArchiveHistoryService);
	}

	// Run batch and simulate request to Archive failure.
	// Rerun an earlier not_completed batch - GET batchhistory and verify it was completed
	@Test
	void testReRunNotCompletedBatch() {
		final var yesterday = LocalDate.now().minusDays(1);
		final var arrayOfArende = new ArrayOfArende();
		final var arende = createArendeObject(List.of(GEO, FASSIT2, TOMTPLBE));
		arrayOfArende.getArende().add(arende);
		final var batchHistory = BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(NOT_COMPLETED).build();
		final var uncompletedBatch = BatchHistory.builder().withStart(yesterday).withEnd(yesterday).withArchiveStatus(COMPLETED).build();

		// Mock
		when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(batchHistory)
			.thenReturn(uncompletedBatch);

		// First run, fails
		final var firstBatchHistory = byggrArchiverService.runBatch(yesterday, yesterday, SCHEDULED, MUNICIPALITY_ID);
		assertThat(firstBatchHistory.getArchiveStatus()).isEqualTo(NOT_COMPLETED);

		// The first attempt
		verify(mockArchiveHistoryService).archive(any(), any(), any(), eq(MUNICIPALITY_ID));

		// ReRun, success
		when(mockBatchHistoryRepository.findById(firstBatchHistory.getId())).thenReturn(Optional.of(batchHistory));

		final var reRunBatchHistory = byggrArchiverService.reRunBatch(firstBatchHistory.getId(), MUNICIPALITY_ID);

		assertThat(reRunBatchHistory.getArchiveStatus()).isEqualTo(COMPLETED);
		assertEquals(firstBatchHistory.getId(), reRunBatchHistory.getId());

		// Both the first batch and the reRun
		verify(mockArchiveHistoryService, times(2)).archive(any(), any(), any(), eq(MUNICIPALITY_ID));
		verify(mockArchiveHistoryService, times(2)).archive(any(), any(), batchHistoryCaptor.capture(), eq(MUNICIPALITY_ID));
		assertThat(batchHistoryCaptor.getAllValues().get(0).getArchiveStatus()).isEqualTo(NOT_COMPLETED);
		assertThat(batchHistoryCaptor.getAllValues().get(1).getArchiveStatus()).isEqualTo(NOT_COMPLETED);
	}

	@Test
	void rerunBatch() {
		final var randomId = randomLong();
		final var start = LocalDate.now().minusDays(7);
		final var end = LocalDate.now().minusDays(7);

		when(mockBatchHistoryRepository.findById(randomId))
			.thenReturn(Optional.of(BatchHistory.builder().withStart(start).withEnd(end).withId(randomId).withArchiveStatus(NOT_COMPLETED).build()));

		when(mockArchiveHistoryService.archive(any(), any(), batchHistoryCaptor.capture(), eq(MUNICIPALITY_ID)))
			.thenReturn(BatchHistory.builder().withStart(start).withEnd(end).withId(randomId).withArchiveStatus(COMPLETED).build());

		byggrArchiverService.reRunBatch(randomId, MUNICIPALITY_ID);

		verify(mockArchiveHistoryService).archive(any(), any(), any(), eq(MUNICIPALITY_ID));
		assertThat(batchHistoryCaptor.getValue().getStart()).isEqualTo(start);
		assertThat(batchHistoryCaptor.getValue().getEnd()).isEqualTo(end);
	}

	@Test
	void rerunBatchThatDoesNotExist() {
		final var randomId = randomLong();

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> byggrArchiverService.reRunBatch(randomId, MUNICIPALITY_ID))
			.satisfies(throwableProblem -> assertThat(throwableProblem.getStatus()).isEqualTo(NOT_FOUND));
	}

	@Test
	void rerunBatchCompleted() {
		final var randomId = randomLong();
		final var start = LocalDate.now().minusDays(7);
		final var end = LocalDate.now().minusDays(7);

		when(mockBatchHistoryRepository.findById(randomId))
			.thenReturn(Optional.of(BatchHistory.builder().withStart(start).withEnd(end).withId(randomId).withArchiveStatus(COMPLETED).build()));

		final var exception = assertThrows(ThrowableProblem.class, () -> byggrArchiverService.reRunBatch(randomId, MUNICIPALITY_ID));

		assertThat(exception.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(exception.getMessage()).isEqualTo("Bad Request: It's not possible to rerun a completed batch.");
	}

	/**
	 * Util method for creating arende-objects
	 *
	 * @param  attachmentCategories - the documents that should be generated
	 * @return                      Arende
	 */
	private Arende createArendeObject(final List<AttachmentCategory> attachmentCategories) {
		final var arrayOfHandelseHandling = new ArrayOfHandelseHandling();
		final var dokumentList = new ArrayList<Dokument>();
		attachmentCategories.forEach(category -> {
			final var dokument = new Dokument();
			dokument.setDokId(String.valueOf(randomInt(999999)));
			dokument.setNamn("Test filnamn");
			final var dokumentFil = new DokumentFil();
			dokumentFil.setFilAndelse("pdf");
			dokument.setFil(dokumentFil);
			dokument.setSkapadDatum(LocalDateTime.now().minusDays(30));

			dokumentList.add(dokument);

			final var handling = new HandelseHandling();
			handling.setTyp(category.name());
			handling.setDokument(dokument);

			arrayOfHandelseHandling.getHandling().add(handling);
		});

		final var handelse = new Handelse();
		handelse.setHandelsetyp(se.sundsvall.byggrarchiver.util.Constants.BYGGR_HANDELSETYP_ARKIV);
		handelse.setHandlingLista(arrayOfHandelseHandling);
		final var arrayOfHandelse = new ArrayOfHandelse();
		arrayOfHandelse.getHandelse().add(handelse);
		final var arende = new Arende();
		arende.setDnr("BYGG 2021-" + randomInt(999999));
		arende.setStatus(se.sundsvall.byggrarchiver.util.Constants.BYGGR_STATUS_AVSLUTAT);
		arende.setHandelseLista(arrayOfHandelse);
		arende.setObjektLista(createArrayOfAbstractArendeObjekt());

		for (final var doc : dokumentList) {
			lenient().doReturn(List.of(doc)).when(mockArendeExportIntegrationService).getDocument(doc.getDokId());
		}

		return arende;
	}

	private ArrayOfAbstractArendeObjekt2 createArrayOfAbstractArendeObjekt() {
		final var fastighet = new Fastighet();
		fastighet.setFnr(123456);
		final var arendeFastighet = new ArendeFastighet();
		arendeFastighet.setFastighet(fastighet);
		arendeFastighet.setArHuvudObjekt(true);
		final var arrayOfAbstractArendeObjekt = new ArrayOfAbstractArendeObjekt2();
		arrayOfAbstractArendeObjekt.getAbstractArendeObjekt().add(arendeFastighet);
		return arrayOfAbstractArendeObjekt;
	}

}
