package se.sundsvall.byggrarchiver.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomArchiveHistory;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomBatchHistory;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.getRandomEnumValue;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;

import se.sundsvall.byggrarchiver.Application;
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

    @MockBean
    private ByggrArchiverService mockByggrArchiverService;
    @MockBean
    private ArchiveHistoryRepository mockArchiveHistoryRepository;
    @MockBean
    private BatchHistoryRepository mockBatchHistoryRepository;

    @Autowired
    private ByggrArchiverResource resource;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getArchiveHistory() {
        when(mockArchiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryId(any(ArchiveStatus.class), anyLong()))
            .thenReturn(List.of(createRandomArchiveHistory(), createRandomArchiveHistory()));

        webTestClient.get()
            .uri("archived/attachments?archiveStatus={archiveStatus}&batchHistoryId={batchHistoryId}", ArchiveStatus.COMPLETED, 1L)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ArchiveHistory.class);
    }

    @Test
    void getArchiveHistory404() {
        when(mockArchiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryId(any(ArchiveStatus.class), anyLong()))
            .thenReturn(List.of());

        webTestClient.get()
            .uri("archived/attachments?archiveStatus={archiveStatus}&batchHistoryId={batchHistoryId}", getRandomEnumValue(ArchiveStatus.class), randomLong())
            .exchange()
            .expectStatus().isNotFound()
            .expectBodyList(Problem.class);
    }

    @Test
    void getBatchHistory() {
        when(mockBatchHistoryRepository.findAll()).thenReturn(List.of(createRandomBatchHistory()));

        webTestClient.get()
            .uri("batch-jobs")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(BatchHistory.class);
    }

    @Test
    void getBatchHistory404() {
        when(mockBatchHistoryRepository.findAll()).thenReturn(List.of());

        webTestClient.get()
            .uri("batch-jobs")
            .exchange()
            .expectStatus().isNotFound()
            .expectBodyList(Problem.class);
    }

    @Test
    void postBatchJob() throws Exception {
        var batchJob = BatchJob.builder()
            .withStart(LocalDate.now().minusDays(3))
            .withEnd(LocalDate.now())
            .build();

        var batchHistory = BatchHistory.builder()
            .withId(randomLong())
            .withBatchTrigger(BatchTrigger.MANUAL)
            .withArchiveStatus(ArchiveStatus.COMPLETED)
            .withStart(batchJob.getStart())
            .withEnd(batchJob.getEnd())
            .withTimestamp(LocalDateTime.now())
            .build();

        when(mockByggrArchiverService.runBatch(any(LocalDate.class), any(LocalDate.class), any(BatchTrigger.class)))
            .thenReturn(batchHistory);

        webTestClient.post()
            .uri("/batch-jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(batchJob)
            .exchange()
            .expectStatus().isOk()
            .expectBody(BatchHistory.class);
    }

    @Test
    void reRunBatchJob() throws Exception {
        when(mockByggrArchiverService.reRunBatch(anyLong())).thenReturn(createRandomBatchHistory());

        webTestClient.post()
            .uri("batch-jobs/{batchHistoryId}/rerun", randomLong())
            .exchange()
            .expectStatus().isOk()
            .expectBody(BatchHistory.class);
    }

    @Test
    void mapToArchiveHistoryResponse_withNullInput() {
        assertThat(resource.mapToArchiveHistoryResponse(null)).isNull();
    }

    @Test
    void mapToArchiveHistoryResponse() {
        var archiveHistory = createRandomArchiveHistory();
        var archiveHistoryResponse = resource.mapToArchiveHistoryResponse(archiveHistory);

        assertThat(archiveHistoryResponse.getDocumentId()).isEqualTo(archiveHistory.getDocumentId());
        assertThat(archiveHistoryResponse.getCaseId()).isEqualTo(archiveHistory.getCaseId());
        assertThat(archiveHistoryResponse.getDocumentName()).isEqualTo(archiveHistory.getDocumentName());
        assertThat(archiveHistoryResponse.getDocumentType()).isEqualTo(archiveHistory.getDocumentType());
        assertThat(archiveHistoryResponse.getArchiveId()).isEqualTo(archiveHistory.getArchiveId());
        assertThat(archiveHistoryResponse.getArchiveUrl()).isEqualTo(archiveHistory.getArchiveUrl());
        assertThat(archiveHistoryResponse.getArchiveStatus()).isEqualTo(archiveHistory.getArchiveStatus());
        assertThat(archiveHistoryResponse.getTimestamp()).isEqualTo(archiveHistory.getTimestamp());
        assertThat(archiveHistoryResponse.getBatchHistory()).isNotNull();
    }

    @Test
    void mapToBatchHistoryResponse_withNullInput() {
        assertThat(resource.mapToBatchHistoryResponse(null)).isNull();
    }

    @Test
    void mapToBatchHistoryResponse() {
        var batchHistory = createRandomBatchHistory();
        var batchHistoryResponse = resource.mapToBatchHistoryResponse(batchHistory);

        assertThat(batchHistoryResponse.getId()).isEqualTo(batchHistory.getId());
        assertThat(batchHistoryResponse.getStart()).isEqualTo(batchHistory.getStart());
        assertThat(batchHistoryResponse.getEnd()).isEqualTo(batchHistory.getEnd());
        assertThat(batchHistoryResponse.getArchiveStatus()).isEqualTo(batchHistory.getArchiveStatus());
        assertThat(batchHistoryResponse.getBatchTrigger()).isEqualTo(batchHistory.getBatchTrigger());
        assertThat(batchHistoryResponse.getTimestamp()).isEqualTo(batchHistory.getTimestamp());
    }
}