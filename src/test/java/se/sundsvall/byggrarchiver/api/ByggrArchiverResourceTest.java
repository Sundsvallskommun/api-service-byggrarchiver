package se.sundsvall.byggrarchiver.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomArchiveHistory;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomBatchHistory;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomBatchHistoryResponse;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.getRandomEnumValue;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.byggrarchiver.Application;
import se.sundsvall.byggrarchiver.api.model.ArchiveHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.BatchHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ByggrArchiverResourceTest {

	private static final String MUNICIPALITY_ID = "2281";

	private static final String BATCH_PATH = "/{municipalityId}/batch-jobs";

	private static final String ARCHIVED_PATH = "/{municipalityId}/archived/attachments?archiveStatus={archiveStatus}&batchHistoryId={batchHistoryId}";

	@MockitoBean
	private ByggrArchiverService mockByggrArchiverService;

	@MockitoBean
	private ArchiveHistoryRepository mockArchiveHistoryRepository;

	@MockitoBean
	private BatchHistoryRepository mockBatchHistoryRepository;

	@Autowired
	private ByggrArchiverResource resource;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getArchiveHistory() {
		when(mockArchiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(any(ArchiveStatus.class), anyLong(), eq(MUNICIPALITY_ID)))
			.thenReturn(List.of(createRandomArchiveHistory(), createRandomArchiveHistory()));

		webTestClient.get()
			.uri(ARCHIVED_PATH, MUNICIPALITY_ID, ArchiveStatus.COMPLETED, 1L)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(ArchiveHistory.class);
	}

	@Test
	void getArchiveHistory404() {
		when(mockArchiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(any(ArchiveStatus.class), anyLong(), eq(MUNICIPALITY_ID)))
			.thenReturn(List.of());

		final var resutlt = webTestClient.get()
			.uri(ARCHIVED_PATH, MUNICIPALITY_ID, getRandomEnumValue(ArchiveStatus.class), randomLong())
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(ArchiveHistoryResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(resutlt).isEmpty();
	}

	@Test
	void getBatchHistory() {
		when(mockBatchHistoryRepository.findAll()).thenReturn(List.of(createRandomBatchHistory()));

		webTestClient.get()
			.uri(BATCH_PATH, MUNICIPALITY_ID)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(BatchHistory.class);
	}

	@Test
	void getBatchHistory404() {
		when(mockBatchHistoryRepository.findAll()).thenReturn(List.of());

		final var result = webTestClient.get()
			.uri(BATCH_PATH, MUNICIPALITY_ID)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(BatchHistoryResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(result).isEmpty();
	}

	@Test
	void postBatchJob() {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.now().minusDays(3))
			.withEnd(LocalDate.now())
			.build();

		final var batchHistory = BatchHistoryResponse.builder()
			.withId(randomLong())
			.withBatchTrigger(BatchTrigger.MANUAL)
			.withArchiveStatus(ArchiveStatus.COMPLETED)
			.withStart(batchJob.getStart())
			.withEnd(batchJob.getEnd())
			.withTimestamp(LocalDateTime.now())
			.build();

		when(mockByggrArchiverService.runBatch(any(LocalDate.class), any(LocalDate.class), any(BatchTrigger.class), eq(MUNICIPALITY_ID)))
			.thenReturn(batchHistory);

		webTestClient.post()
			.uri(BATCH_PATH, MUNICIPALITY_ID)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(batchJob)
			.exchange()
			.expectStatus().isOk()
			.expectBody(BatchHistory.class);
	}

	@Test
	void reRunBatchJob() {
		when(mockByggrArchiverService.reRunBatch(anyLong(), eq(MUNICIPALITY_ID))).thenReturn(createRandomBatchHistoryResponse());

		webTestClient.post()
			.uri(BATCH_PATH + "/{batchHistoryId}/rerun", MUNICIPALITY_ID, randomLong())
			.exchange()
			.expectStatus().isOk()
			.expectBody(BatchHistory.class);
	}

}
