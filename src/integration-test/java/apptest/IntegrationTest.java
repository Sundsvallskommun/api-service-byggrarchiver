package apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.SCHEDULED;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.sundsvall.byggrarchiver.Application;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(
	files = "classpath:/IntegrationTest/",
	classes = Application.class
)
class IntegrationTest extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";

	private static final String BATCH_PATH = "/2281/batch-jobs";

	private static final String ARCHIVED_PATH = "/2281/archived/attachments";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ArchiveHistoryRepository archiveHistoryRepository;

	@Autowired
	private BatchHistoryRepository batchHistoryRepository;

	@BeforeEach
	void beforeEach() {
		wiremock.resetAll();

		// Clear db between tests
		archiveHistoryRepository.deleteAll();
		batchHistoryRepository.deleteAll();
	}

	// POST batch and then GET batch history and archive histories - verify that the correct is returned
	@Test
	void test01_standardPostBatchJob() throws JsonProcessingException, ClassNotFoundException {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.now().minusDays(1))
			.withEnd(LocalDate.now().minusDays(1))
			.build();

		// POST batchJob
		final var postBatchHistory = postBatchJob(batchJob);

		// GET batchJob
		final var postBatchHistoryList = List.of(
			setupCall()
				.withHttpMethod(GET)
				.withServicePath(BATCH_PATH)
				.withExpectedResponseStatus(OK)
				.sendRequestAndVerifyResponse()
				.andReturnBody(BatchHistory[].class));

		assertThat(postBatchHistoryList).contains(postBatchHistory);

		// GET archiveHistory
		final var getArchiveHistoryList = List.of(
			setupCall()
				.withHttpMethod(GET)
				.withServicePath(ARCHIVED_PATH + "?batchHistoryId=" + postBatchHistory.getId())
				.withExpectedResponseStatus(OK)
				.sendRequestAndVerifyResponse()
				.andReturnBody(ArchiveHistory[].class));

		// GET archiveHistory with status COMPLETED
		final var getArchiveHistoryListCompleted = List.of(
			setupCall()
				.withHttpMethod(GET)
				.withServicePath(ARCHIVED_PATH + "?status=" + COMPLETED)
				.withExpectedResponseStatus(OK)
				.sendRequestAndVerifyResponse()
				.andReturnBody(ArchiveHistory[].class));

		assertThat(getArchiveHistoryList).isEqualTo(getArchiveHistoryListCompleted).allSatisfy(archiveHistory -> {
			assertThat(archiveHistory.getBatchHistory()).isEqualTo(postBatchHistory);
			assertThat(archiveHistory.getArchiveStatus()).isEqualTo(COMPLETED);
		});

		getArchiveHistoryList.forEach(archiveHistory -> {
			archiveHistory.setMunicipalityId(MUNICIPALITY_ID);
			archiveHistory.getBatchHistory().setMunicipalityId(MUNICIPALITY_ID);
		});
		assertThat(archiveHistoryRepository.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(postBatchHistory.getId(), MUNICIPALITY_ID)).isEqualTo(getArchiveHistoryList);
	}

	// POST batch and then GET batch history and archive histories - verify that the correct is returned
	@Test
	void test02_standardPostBatchJob_zeroArchivedDocs() throws JsonProcessingException, ClassNotFoundException {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.parse("2021-01-01"))
			.withEnd(LocalDate.parse("2021-01-01"))
			.build();

		// POST batchJob
		final var postBatchHistory = postBatchJob(batchJob);

		// GET archiveHistory
		setupCall()
			.withHttpMethod(GET)
			.withServicePath(ARCHIVED_PATH + "?batchHistoryId=" + postBatchHistory.getId())
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	@Test
	void test03_standardPostBatchJobNullDate() throws JsonProcessingException {
		final var batchJob = new BatchJob();

		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH)
			.withRequest(objectMapper.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	@Test
	void test04_getBatchHistoryNotFound() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath(BATCH_PATH)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	// Rerun an earlier batch
	@Test
	void test05_rerun() {
		final var batchHistory = BatchHistory.builder()
			.withArchiveStatus(NOT_COMPLETED)
			.withStart(LocalDate.now().minusDays(2))
			.withEnd(LocalDate.now())
			.withBatchTrigger(SCHEDULED)
			.build();

		batchHistoryRepository.save(batchHistory);

		// POST rerun
		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH + "/" + batchHistory.getId() + "/rerun")
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	// Rerun an earlier completed batch - verify it did not run
	@Test
	void test06_rerunWithCompletedBatch() throws JsonProcessingException, ClassNotFoundException {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.now().minusDays(1))
			.withEnd(LocalDate.now().minusDays(1))
			.build();

		// POST batchJob
		final var postBatchHistoryList = postBatchJob(batchJob);

		assertThat(postBatchHistoryList.getArchiveStatus()).isEqualTo(COMPLETED);

		// POST rerun
		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH + "/" + postBatchHistoryList.getId() + "/rerun")
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	// Rerun a non-existing batch
	@Test
	void test07_rerunNonExistingBatch() {
		final var randomNumber = randomInt(999999);

		// POST rerun
		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH + "/" + randomNumber + "/rerun")
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	// Try to run batch with start today and end tomorrow
	@Test
	void test08_runBatchForFutureDateV1() throws JsonProcessingException {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.now())
			.withEnd(LocalDate.now().plusDays(1))
			.build();

		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH)
			.withRequest(objectMapper.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	// Try to run batch with start and end tomorrow
	@Test
	void test09_runBatchForFutureDateV2() throws JsonProcessingException {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.now().plusDays(1))
			.withEnd(LocalDate.now().plusDays(1))
			.build();

		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH)
			.withRequest(objectMapper.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	// Try to run batch with start date later than end date
	@Test
	void test10_runBatchWithEndBeforeStart() throws JsonProcessingException {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.now().minusDays(1))
			.withEnd(LocalDate.now().minusDays(2))
			.build();

		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH)
			.withRequest(objectMapper.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	// Test exception from GetUpdatedArenden - Should return http 500
	@Test
	void test11_errorFromGetUpdatedArenden() throws JsonProcessingException {
		final var batchJob = BatchJob.builder()
			.withStart(LocalDate.parse("1999-01-01"))
			.withEnd(LocalDate.parse("1999-01-01"))
			.build();

		setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH)
			.withRequest(objectMapper.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(SERVICE_UNAVAILABLE)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(ARCHIVED_PATH)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response2.json")
			.sendRequestAndVerifyResponse()
			.verifyAllStubs();
	}

	//
	@Test
	void test12_missingExtension() throws JsonProcessingException, ClassNotFoundException {
		postBatchJob(BatchJob.builder()
			.withStart(LocalDate.now())
			.withEnd(LocalDate.now())
			.build());
	}

	@Test
	void test13_extensionError() throws JsonProcessingException, ClassNotFoundException {
		postBatchJob(BatchJob.builder()
			.withStart(LocalDate.now())
			.withEnd(LocalDate.now())
			.build());
	}

	private BatchHistory postBatchJob(final BatchJob batchJob) throws JsonProcessingException, ClassNotFoundException {
		return setupCall()
			.withHttpMethod(POST)
			.withServicePath(BATCH_PATH)
			.withRequest(objectMapper.writeValueAsString(batchJob))
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse()
			.andReturnBody(BatchHistory.class);
	}

	@Override
	public boolean verifyAllStubs() {
		final var unmatchedRequests = wiremock.findAllUnmatchedRequests();
		if (!unmatchedRequests.isEmpty()) {
			final var unmatchedUrls = unmatchedRequests.stream()
				.map(LoggedRequest::getUrl)
				.toList();

			throw new AssertionError(String.format("The following requests was not matched: %s", unmatchedUrls));
		}

		return true;
	}

}
