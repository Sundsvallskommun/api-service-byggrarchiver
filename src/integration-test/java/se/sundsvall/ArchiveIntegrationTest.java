package se.sundsvall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchHistory;
import se.sundsvall.vo.BatchJob;
import se.sundsvall.vo.Status;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class ArchiveIntegrationTest {

    static ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Inject
    ArchiveDao archiveDao;

    static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8090).usingFilesUnderDirectory("src/integration-test/resources"));

    @BeforeAll
    public static void beforeAll() {
        wireMockServer.start();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }


    // Rerun archive for a specific archivehistory that was completed and verify it did not run
    // Rerun archive for a specific archivehistory that was not_completed and verify that the same db-row was completed
    // Try to run batch for future-date
    // Try to run batch with start date later than end date
    // Rerun a batch that failed and verify that the documents that was not completed gets completed and vice versa.

    // POST batch and then GET batchhistory and archivehistories - verify that the correct is returned
    @Test
    void testScheduledJob() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().minusDays(1));
        batchJob.setEnd(LocalDate.now().minusDays(1));

        // POST batchJob
        BatchHistory postBatchHistoryList = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(BatchHistory.class);

        // GET archiveHistory
        List<ArchiveHistory> getArchiveHistoryList = Arrays.asList(given()
                .queryParam("batchHistoryId", postBatchHistoryList.getId())
                .when().get("archived/attachments")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode()).extract().as(ArchiveHistory[].class));

        getArchiveHistoryList.forEach(ah -> Assertions.assertEquals(postBatchHistoryList, ah.getBatchHistory()));
        getArchiveHistoryList.forEach(ah -> Assertions.assertEquals(Status.COMPLETED, ah.getStatus()));
        Assertions.assertEquals(archiveDao.getArchiveHistories(postBatchHistoryList.getId()), getArchiveHistoryList);
    }

    // Rerun an earlier completed batch - verify it did not run
    @Test
    void testRerunWithCompletedBatch() throws JsonProcessingException {

        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().minusDays(1));
        batchJob.setEnd(LocalDate.now().minusDays(1));

        // POST batchJob
        BatchHistory postBatchHistoryList = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(BatchHistory.class);

        Assertions.assertEquals(Status.COMPLETED, postBatchHistoryList.getStatus());

        // PUT batchJob (reRun)
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .pathParams("batchHistoryId", postBatchHistoryList.getId())
                .when().post("batch-jobs/{batchHistoryId}/rerun")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(containsString(Constants.IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH));
    }

    @Test
    void testErrorFromCaseManagement() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.parse("2021-01-01"));
        batchJob.setEnd(LocalDate.parse("2021-01-01"));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .assertThat().body(containsString(Constants.ERR_MSG_UNHANDLED_EXCEPTION));
    }

}