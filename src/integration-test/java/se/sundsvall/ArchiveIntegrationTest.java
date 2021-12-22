package se.sundsvall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.filter.log.LogDetail;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.casemanagement.CaseManagementService;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.*;

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
    Archiver archiver;

    @Inject
    ArchiveDao archiveDao;

    @RestClient
    CaseManagementService caseManagementService;

    @InjectMock
    @RestClient
    ArchiveService archiveServiceMock;

    static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8090).usingFilesUnderDirectory("src/integration-test/resources"));

    @BeforeAll
    public static void beforeAll() {
        wireMockServer.start();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    void testScheduledJob() throws ApplicationException, ServiceException {
        archiver.archiveByggrAttachments(LocalDate.now().minusDays(1), LocalDate.now(), BatchTrigger.SCHEDULED);
        List<ArchiveHistory> archiveHistories = archiveDao.getArchiveHistory();
        System.out.println(archiveHistories);
        archiveHistories.forEach(ah -> Assertions.assertEquals(Status.COMPLETED, ah.getStatus()));
    }

    @Test
    void testErrorFromCaseManagement() throws ServiceException, JsonProcessingException {
        Mockito.when(caseManagementService.getDocuments(any(), any(), any())).thenThrow(ServiceException.create("{\"type\":\"https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4\",\"status\":404,\"title\":\"Not Found\",\"detail\":\"RESTEASY003210: Could not find resource for full path: http://microservices-test.sundsvall.se/cases/closed/documents/archive\"}", null, Response.Status.NOT_FOUND));

        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().minusDays(10));
        batchJob.setEnd(LocalDate.now().minusDays(5));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-job")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .assertThat().body(containsString(Constants.ERR_MSG_UNHANDLED_EXCEPTION));
    }

    @Test
    void testErrorFromArchive() throws  ServiceException, JsonProcessingException {
        Mockito.when(archiveServiceMock.postArchive(any())).thenThrow(ServiceException.create("{\n" +
                "  \"httpCode\": 500,\n" +
                "  \"message\": \"Service error\",\n" +
                "  \"technicalDetails\": {\n" +
                "    \"rootCode\": 500,\n" +
                "    \"rootCause\": \"Internal Server Error\",\n" +
                "    \"serviceId\": \"api-service-archive\",\n" +
                "    \"requestId\": null,\n" +
                "    \"details\": [\n" +
                "      \"Error invoking subclass method\",\n" +
                "      \"Request: /documents\"\n" +
                "    ]\n" +
                "  }\n" +
                "}", null, Response.Status.INTERNAL_SERVER_ERROR));

        // Test
        BatchJob batchJob = new BatchJob();
        batchJob.setStart(LocalDate.now().minusDays(1));
        batchJob.setEnd(LocalDate.now());

        List<ArchiveHistory> archiveHistories = Arrays.asList(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(batchJob))
                .when().post("/batch-job")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(Response.Status.OK.getStatusCode()).extract().as(ArchiveHistory[].class));

        BatchHistory batchHistory = archiveDao.getBatchHistory(archiveHistories.get(0).getBatchHistory().getId());
        Assertions.assertEquals(Status.NOT_COMPLETED, batchHistory.getStatus());
    }
}