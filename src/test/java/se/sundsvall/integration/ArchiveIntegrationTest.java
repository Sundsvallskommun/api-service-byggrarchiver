package se.sundsvall.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.sundsvall.ArchiveDao;
import se.sundsvall.TestDao;
import se.sundsvall.integration.support.WireMockLifecycleManager;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.*;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@QuarkusTestResource(WireMockLifecycleManager.class)
class ArchiveIntegrationTest {

    static ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Inject
    TestDao testDao;

    @Inject
    ArchiveDao archiveDao;

    @BeforeEach
    void beforeEach() {
        // Clear db between tests
        testDao.deleteAllFromAllTables();
    }

    // POST batch and then GET batchhistory and archivehistories - verify that the correct is returned
    @Test
    void testStandardPostBatchJob() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().minusDays(1));
        batchJob.setEnd(LocalDate.now().minusDays(1));

        // POST batchJob
        BatchHistory postBatchHistory = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(BatchHistory.class);

        // GET batchJob
        List<BatchHistory> postBatchHistoryList = Arrays.asList(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().get("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(BatchHistory[].class));

        Assertions.assertTrue(postBatchHistoryList.contains(postBatchHistory));

        // GET archiveHistory
        List<ArchiveHistory> getArchiveHistoryList = Arrays.asList(given()
                .queryParam("batchHistoryId", postBatchHistory.getId())
                .when().get("archived/attachments")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode()).extract().as(ArchiveHistory[].class));

        // GET archiveHistory with status COMPLETED
        List<ArchiveHistory> getArchiveHistoryListCompleted = Arrays.asList(given()
                .queryParam("status", Status.COMPLETED)
                .when().get("archived/attachments")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode()).extract().as(ArchiveHistory[].class));

        Assertions.assertEquals(getArchiveHistoryList, getArchiveHistoryListCompleted);
        getArchiveHistoryList.forEach(ah -> Assertions.assertEquals(postBatchHistory, ah.getBatchHistory()));
        getArchiveHistoryList.forEach(ah -> Assertions.assertEquals(Status.COMPLETED, ah.getStatus()));
        Assertions.assertEquals(archiveDao.getArchiveHistories(postBatchHistory.getId()), getArchiveHistoryList);
    }

    // POST batch and then GET batchhistory and archivehistories - verify that the correct is returned
    @Test
    void testStandardPostBatchJobZeroArchivedDocs() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.parse("2021-01-01"));
        batchJob.setEnd(LocalDate.parse("2021-01-01"));

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
        given()
                .queryParam("batchHistoryId", postBatchHistoryList.getId())
                .when().get("archived/attachments")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(containsString("ArchiveHistory not found"));
    }

    @Test
    void testGetBatchHistoryNotFound() {
        given()
                .when().get("batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(containsString("BatchHistory not found"));
    }

    // Rerun an earlier batch
    @Test
    void testRerun() throws JsonProcessingException {

        BatchHistory batchHistory = new BatchHistory();
        batchHistory.setStatus(Status.NOT_COMPLETED);
        batchHistory.setStart(LocalDate.now().minusDays(2));
        batchHistory.setEnd(LocalDate.now());
        batchHistory.setBatchTrigger(BatchTrigger.SCHEDULED);

        archiveDao.postBatchHistory(batchHistory);

        // PUT batchJob (reRun)
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .pathParams("batchHistoryId", batchHistory.getId())
                .when().post("batch-jobs/{batchHistoryId}/rerun")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode());
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

    // Rerun a non existing batch
    @Test
    void testRerunNonExistingBatch() throws JsonProcessingException {
        int randomNumber = new Random().nextInt(999999);

        // PUT batchJob (reRun)
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .pathParams("batchHistoryId", randomNumber)
                .when().post("batch-jobs/{batchHistoryId}/rerun")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(containsString("Can't find BatchHistory with ID: " + randomNumber));
    }

    // Try to run batch with start today and end tomorrow
    @Test
    void testRunBatchForFutureDateV1() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now());
        batchJob.setEnd(LocalDate.now().plusDays(1));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(containsString("must be a date in the past or in the present"))
                .and().body(containsString("batchJob.end"));
    }

    // Try to run batch with start and end tomorrow
    @Test
    void testRunBatchForFutureDateV2() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().plusDays(1));
        batchJob.setEnd(LocalDate.now().plusDays(1));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(containsString("must be a date in the past or in the present"))
                .and().body(containsString("batchJob.start"))
                .and().body(containsString("batchJob.end"));
    }

    // Try to run batch with start date later than end date
    @Test
    void testRunBatchWithEndBeforeStart() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().minusDays(1));
        batchJob.setEnd(LocalDate.now().minusDays(2));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(containsString(Constants.END_CAN_NOT_BE_BEFORE_START))
                .and().body(containsString("postBatchJob.batchJob"));
    }

    @Test
    void testStandardPostBatchJobNullDate() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();

        // POST batchJob
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(containsString("must not be null"))
                .and().body(containsString("batchJob.start"))
                .and().body(containsString("batchJob.end"));
    }


    // Test exception from GetUpdatedArenden - Should return http 500
    @Test
    void testErrorFromGetUpdatedArenden() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.parse("1999-01-01"));
        batchJob.setEnd(LocalDate.parse("1999-01-01"));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-jobs")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .assertThat().body(containsString(Constants.ERR_MSG_UNHANDLED_EXCEPTION));

        // GET archiveHistory
        given()
                .when().get("archived/attachments")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

//    TODO - Test exception from GetDocument - Should return http 500
//    @Test
//    void testErrorFromGetDocument() throws JsonProcessingException {

//    }


}