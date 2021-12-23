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
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.ArchiveHistory;
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
import static org.mockito.ArgumentMatchers.any;

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

    // POST batch and then GET batchhistory and archivehistories - verify that the correct is returned
    // Rerun an earlier completed batch - verify it did not run
    // Rerun an earlier not_completed batch - GET batchhistory and verify it was completed
    // Rerun archive for a specific archivehistory that was completed and verify it did not run
    // Rerun archive for a specific archivehistory that was not_completed and verify that the same db-row was completed

    @Test
    void testScheduledJob() throws JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().minusDays(1));
        batchJob.setEnd(LocalDate.now());

        Arrays.asList(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-job")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode()).extract().as(ArchiveHistory[].class));

        List<ArchiveHistory> archiveHistories = archiveDao.getArchiveHistories();
        System.out.println(archiveHistories);
        archiveHistories.forEach(ah -> Assertions.assertEquals(Status.COMPLETED, ah.getStatus()));
    }

    @Test
    void testErrorFromCaseManagement() throws ServiceException, JsonProcessingException {
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.parse("2021-01-01"));
        batchJob.setEnd(LocalDate.parse("2021-01-01"));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-job")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .assertThat().body(containsString(Constants.ERR_MSG_UNHANDLED_EXCEPTION));
    }

}