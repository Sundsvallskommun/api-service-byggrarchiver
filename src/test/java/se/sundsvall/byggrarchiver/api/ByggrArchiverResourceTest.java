package se.sundsvall.byggrarchiver.api;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomArchiveHistory;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomBatchHistory;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.getRandomOfEnum;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class ByggrArchiverResourceTest {
    @MockBean
    private ByggrArchiverService byggrArchiverServiceMock;

    @MockBean
    private ArchiveHistoryRepository archiveHistoryRepositoryMock;

    @MockBean
    private BatchHistoryRepository batchHistoryRepositoryMock;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getArchiveHistory() {

        doReturn(List.of(createRandomArchiveHistory(), createRandomArchiveHistory())).when(archiveHistoryRepositoryMock).getArchiveHistoriesByArchiveStatusAndBatchHistoryId(any(), any());

        webTestClient.get().uri("archived/attachments?archiveStatus={archiveStatus}&batchHistoryId={batchHistoryId}", ArchiveStatus.COMPLETED, 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ArchiveHistory.class);

    }

    @Test
    void getArchiveHistory404() {
        ArchiveStatus archiveStatus = (ArchiveStatus) getRandomOfEnum(ArchiveStatus.class);
        Long batchHistoryId = new Random().nextLong();
        doReturn(new ArrayList()).when(archiveHistoryRepositoryMock).getArchiveHistoriesByArchiveStatusAndBatchHistoryId(archiveStatus, batchHistoryId);

        webTestClient.get().uri("archived/attachments?archiveStatus={archiveStatus}&batchHistoryId={batchHistoryId}", archiveStatus, batchHistoryId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBodyList(Problem.class);
    }

    @Test
    void getBatchHistory() {
        doReturn(List.of(createRandomBatchHistory())).when(batchHistoryRepositoryMock).findAll();

        webTestClient.get().uri("batch-jobs")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BatchHistory.class);
    }

    @Test
    void getBatchHistory404() {
        doReturn(new ArrayList()).when(batchHistoryRepositoryMock).findAll();

        webTestClient.get().uri("batch-jobs")
                .exchange()
                .expectStatus().isNotFound()
                .expectBodyList(Problem.class);
    }

    @Test
    void postBatchJob() throws Exception {
        BatchJob batchJobRequest = BatchJob.builder()
                .start(LocalDate.now().minusDays(3))
                .end(LocalDate.now())
                .build();
        BatchHistory batchHistory = BatchHistory.builder()
                .id(new Random().nextLong())
                .batchTrigger(BatchTrigger.MANUAL)
                .archiveStatus(ArchiveStatus.COMPLETED)
                .start(batchJobRequest.getStart())
                .end(batchJobRequest.getEnd())
                .timestamp(LocalDateTime.now())
                .build();

        doReturn(batchHistory).when(byggrArchiverServiceMock).runBatch(batchJobRequest.getStart(), batchJobRequest.getEnd(), BatchTrigger.MANUAL);

        webTestClient.post().uri("/batch-jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(batchJobRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BatchHistory.class);
    }

    @Test
    void reRunBatchJob() throws Exception {
        Long batchHistoryId = new Random().nextLong();
        doReturn(createRandomBatchHistory()).when(byggrArchiverServiceMock).reRunBatch(batchHistoryId);

        webTestClient.post().uri("batch-jobs/{batchHistoryId}/rerun", batchHistoryId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BatchHistory.class);
    }
}
