package apptest;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import se.sundsvall.byggrarchiver.Application;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(
	files = "classpath:/IntegrationTest/",
	classes = Application.class)
class IntegrationTest extends AbstractAppTest {

	public static final String RESPONSE_JSON = "response.json";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
		.enable(SerializationFeature.INDENT_OUTPUT)
		.registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	@Autowired
	private ArchiveHistoryRepository archiveHistoryRepository;

	@Autowired
	private BatchHistoryRepository batchHistoryRepository;

	@BeforeEach
	void beforeEach() {
		this.wiremock.resetAll();
		// Clear db between tests
		archiveHistoryRepository.deleteAll();
		batchHistoryRepository.deleteAll();
	}

	// POST batch and then GET batch history and archive histories - verify that the correct is returned
	@Test
	void testStandardPostBatchJob() throws JsonProcessingException, ClassNotFoundException {
		final BatchJob batchJob = new BatchJob();
		batchJob.setStart(LocalDate.now().minusDays(1));
		batchJob.setEnd(LocalDate.now().minusDays(1));

		// POST batchJob
		final var postBatchHistory = postBatchJob(batchJob);

		// GET batchJob
		final List<BatchHistory> postBatchHistoryList = Arrays.asList(
			setupCall()
				.withHttpMethod(HttpMethod.GET)
				.withServicePath("/batch-jobs")
				.withExpectedResponseStatus(HttpStatus.OK)
				.sendRequestAndVerifyResponse()
				.andReturnBody(BatchHistory[].class));

		Assertions.assertTrue(postBatchHistoryList.contains(postBatchHistory));

		// GET archiveHistoryb
		final List<ArchiveHistory> getArchiveHistoryList = Arrays.asList(
			setupCall()
				.withHttpMethod(HttpMethod.GET)
				.withServicePath("/archived/attachments?batchHistoryId=" + postBatchHistory.getId())
				.withExpectedResponseStatus(HttpStatus.OK)
				.sendRequestAndVerifyResponse()
				.andReturnBody(ArchiveHistory[].class));

		// GET archiveHistory with status COMPLETED
		final List<ArchiveHistory> getArchiveHistoryListCompleted = Arrays.asList(
			setupCall()
				.withHttpMethod(HttpMethod.GET)
				.withServicePath("/archived/attachments?status=" + ArchiveStatus.COMPLETED)
				.withExpectedResponseStatus(HttpStatus.OK)
				.sendRequestAndVerifyResponse()
				.andReturnBody(ArchiveHistory[].class));

		Assertions.assertEquals(getArchiveHistoryList, getArchiveHistoryListCompleted);
		getArchiveHistoryList.forEach(ah -> Assertions.assertEquals(postBatchHistory, ah.getBatchHistory()));
		getArchiveHistoryList.forEach(ah -> Assertions.assertEquals(ArchiveStatus.COMPLETED, ah.getArchiveStatus()));
		Assertions.assertEquals(archiveHistoryRepository.getArchiveHistoriesByBatchHistoryId(postBatchHistory.getId()), getArchiveHistoryList);
	}

	// POST batch and then GET batch history and archive histories - verify that the correct is returned
	@Test
	void testStandardPostBatchJobZeroArchivedDocs() throws JsonProcessingException, ClassNotFoundException {
		final BatchJob batchJob = new BatchJob();
		batchJob.setStart(LocalDate.parse("2021-01-01"));
		batchJob.setEnd(LocalDate.parse("2021-01-01"));

		// POST batchJob
		final var postBatchHistory = postBatchJob(batchJob);

		// GET archiveHistory
		setupCall()
			.withHttpMethod(HttpMethod.GET)
			.withServicePath("/archived/attachments?batchHistoryId=" + postBatchHistory.getId())
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void testGetBatchHistoryNotFound() {
		setupCall()
			.withHttpMethod(HttpMethod.GET)
			.withServicePath("/batch-jobs")
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	// Rerun an earlier batch
	@Test
	void testRerun() {

		final BatchHistory batchHistory = new BatchHistory();
		batchHistory.setArchiveStatus(ArchiveStatus.NOT_COMPLETED);
		batchHistory.setStart(LocalDate.now().minusDays(2));
		batchHistory.setEnd(LocalDate.now());
		batchHistory.setBatchTrigger(BatchTrigger.SCHEDULED);

		batchHistoryRepository.save(batchHistory);

		// POST rerun
		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs/" + batchHistory.getId() + "/rerun")
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.OK)
			.sendRequestAndVerifyResponse();

	}

	// Rerun an earlier completed batch - verify it did not run
	@Test
	void testRerunWithCompletedBatch() throws JsonProcessingException, ClassNotFoundException {

		final BatchJob batchJob = new BatchJob();
		batchJob.setStart(LocalDate.now().minusDays(1));
		batchJob.setEnd(LocalDate.now().minusDays(1));

		// POST batchJob
		final BatchHistory postBatchHistoryList = postBatchJob(batchJob);

		Assertions.assertEquals(ArchiveStatus.COMPLETED, postBatchHistoryList.getArchiveStatus());

		// POST rerun
		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs/" + postBatchHistoryList.getId() + "/rerun")
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.BAD_REQUEST)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	// Rerun a non-existing batch
	@Test
	void testRerunNonExistingBatch() {
		final int randomNumber = new Random().nextInt(999999);

		// POST rerun
		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs/" + randomNumber + "/rerun")
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	// Try to run batch with start today and end tomorrow
	@Test
	void testRunBatchForFutureDateV1() throws JsonProcessingException {
		final BatchJob batchJob = new BatchJob();
		batchJob.setStart(LocalDate.now());
		batchJob.setEnd(LocalDate.now().plusDays(1));

		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs")
			.withRequest(OBJECT_MAPPER.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.BAD_REQUEST)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	// Try to run batch with start and end tomorrow
	@Test
	void testRunBatchForFutureDateV2() throws JsonProcessingException {
		final BatchJob batchJob = new BatchJob();
		batchJob.setStart(LocalDate.now().plusDays(1));
		batchJob.setEnd(LocalDate.now().plusDays(1));

		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs")
			.withRequest(OBJECT_MAPPER.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.BAD_REQUEST)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	// Try to run batch with start date later than end date
	@Test
	void testRunBatchWithEndBeforeStart() throws JsonProcessingException {
		final BatchJob batchJob = new BatchJob();
		batchJob.setStart(LocalDate.now().minusDays(1));
		batchJob.setEnd(LocalDate.now().minusDays(2));

		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs")
			.withRequest(OBJECT_MAPPER.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.BAD_REQUEST)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void testStandardPostBatchJobNullDate() throws JsonProcessingException {
		final BatchJob batchJob = new BatchJob();

		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs")
			.withRequest(OBJECT_MAPPER.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.BAD_REQUEST)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();
	}

	// Test exception from GetUpdatedArenden - Should return http 500
	@Test
	void testErrorFromGetUpdatedArenden() throws JsonProcessingException {
		final BatchJob batchJob = new BatchJob();
		batchJob.setStart(LocalDate.parse("1999-01-01"));
		batchJob.setEnd(LocalDate.parse("1999-01-01"));

		setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs")
			.withRequest(OBJECT_MAPPER.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
			.withExpectedResponse(RESPONSE_JSON)
			.sendRequestAndVerifyResponse();

		setupCall()
			.withHttpMethod(HttpMethod.GET)
			.withServicePath("/archived/attachments")
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse("response2.json")
			.sendRequestAndVerifyResponse();
	}

	//
	@Test
	void testMissingExtension() throws JsonProcessingException, ClassNotFoundException {

		postBatchJob(BatchJob.builder()
			.start(LocalDate.now())
			.end(LocalDate.now())
			.build());

		// GetUpdatedArenden returns 2 documents
		verify(2, postRequestedFor(urlEqualTo("/archive/1.0/archive/byggr")).withRequestBody(containing("\"extension\" : \".docx\"")));
	}

	@Test
	void testExtensionError() throws JsonProcessingException, ClassNotFoundException {
		postBatchJob(BatchJob.builder()
			.start(LocalDate.now())
			.end(LocalDate.now())
			.build());

		// Verify that email is sent
		verify(postRequestedFor(urlEqualTo("/messaging/1.2/email")).withRequestBody(containing("Manuell hantering krävs")));
	}

	private BatchHistory postBatchJob(BatchJob batchJob) throws JsonProcessingException, ClassNotFoundException {
		return setupCall()
			.withHttpMethod(HttpMethod.POST)
			.withServicePath("/batch-jobs")
			.withRequest(OBJECT_MAPPER.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(HttpStatus.OK)
			.sendRequestAndVerifyResponse()
			.andReturnBody(BatchHistory.class);
	}

	@Override
	public boolean verifyAllStubs() {
		final List<LoggedRequest> unmatchedRequests = this.wiremock.findAllUnmatchedRequests();
		if (!unmatchedRequests.isEmpty()) {
			final List<String> unmatchedUrls = unmatchedRequests.stream().map(LoggedRequest::getUrl).toList();
			throw new AssertionError(String.format("The following requests was not matched: %s", unmatchedUrls));
		}

		return true;
	}
}
