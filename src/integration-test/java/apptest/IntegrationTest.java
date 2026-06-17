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
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.ARCHIVE_REJECTED_FORMAT;
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.BYGGR_FETCH_ERROR;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import se.sundsvall.byggrarchiver.Application;
import se.sundsvall.byggrarchiver.api.model.ArchiveFailureResponse;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.integration.db.ArchiveFailureRepository;
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

	@Autowired
	private ArchiveFailureRepository archiveFailureRepository;

	@BeforeEach
	void beforeEach() {
		wiremock.resetAll();

		// Clear db between tests
		archiveHistoryRepository.deleteAll();
		batchHistoryRepository.deleteAll();
		archiveFailureRepository.deleteAll();
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
			.sendRequestAndVerifyResponse();
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
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_getBatchHistoryNotFound() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath(BATCH_PATH)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
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
			.sendRequestAndVerifyResponse();
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
			.sendRequestAndVerifyResponse();
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
			.sendRequestAndVerifyResponse();
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
			.sendRequestAndVerifyResponse();
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
			.sendRequestAndVerifyResponse();
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
			.sendRequestAndVerifyResponse();

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(ARCHIVED_PATH)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response2.json")
			.sendRequestAndVerifyResponse();
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

	// Batch where one document is rejected by the archive service (HTTP 500 "File format validation failed.") while the
	// rest archive OK - verify one document is COMPLETED and that the rejection is recorded in the archive_failure table.
	@Test
	void test14_failingBatch() throws JsonProcessingException, ClassNotFoundException {
		// Fixed date window whose GetUpdatedArenden fixture has BatchEnd == the window end, so the batch loop runs only
		// two passes (avoids the failing document being re-archived enough times to trip the archive circuit breaker).
		final var postBatchHistory = postBatchJob(BatchJob.builder()
			.withStart(LocalDate.parse("2021-12-17"))
			.withEnd(LocalDate.parse("2021-12-17"))
			.build());

		final var archiveHistories = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(postBatchHistory.getId(), MUNICIPALITY_ID);

		// One document (431169) archived successfully, while the keyed document (433467) was rejected and not completed
		assertThat(archiveHistories)
			.anySatisfy(archiveHistory -> {
				assertThat(archiveHistory.getDocumentId()).isEqualTo("431169");
				assertThat(archiveHistory.getArchiveStatus()).isEqualTo(COMPLETED);
			})
			.anySatisfy(archiveHistory -> {
				assertThat(archiveHistory.getDocumentId()).isEqualTo("433467");
				assertThat(archiveHistory.getArchiveStatus()).isEqualTo(NOT_COMPLETED);
			});

		// The rejection was recorded in the append-only audit table
		final var archiveFailures = archiveFailureRepository.findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(postBatchHistory.getId(), MUNICIPALITY_ID, null);

		assertThat(archiveFailures)
			.isNotEmpty()
			.allSatisfy(archiveFailure -> {
				assertThat(archiveFailure.getDocumentId()).isEqualTo("433467");
				assertThat(archiveFailure.getCaseId()).isEqualTo("BYGG 2018-000026");
				assertThat(archiveFailure.getFailureCategory()).isEqualTo(ARCHIVE_REJECTED_FORMAT);
			});

		// ... and is readable through the fallout endpoint
		final var fallout = setupCall()
			.withHttpMethod(GET)
			.withServicePath(BATCH_PATH + "/" + postBatchHistory.getId() + "/fallout")
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse()
			.andReturnBody(ArchiveFailureResponse[].class);

		assertThat(fallout)
			.isNotEmpty()
			.allSatisfy(archiveFailure -> {
				assertThat(archiveFailure.getBatchHistoryId()).isEqualTo(postBatchHistory.getId());
				assertThat(archiveFailure.getDocumentId()).isEqualTo("433467");
				assertThat(archiveFailure.getCaseId()).isEqualTo("BYGG 2018-000026");
				assertThat(archiveFailure.getFailureCategory()).isEqualTo(ARCHIVE_REJECTED_FORMAT);
			});

		// The category filter excludes non-matching categories
		final var noFileTooLargeFallout = setupCall()
			.withHttpMethod(GET)
			.withServicePath(BATCH_PATH + "/" + postBatchHistory.getId() + "/fallout?category=FILE_TOO_LARGE")
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse()
			.andReturnBody(ArchiveFailureResponse[].class);

		assertThat(noFileTooLargeFallout).isEmpty();
	}

	// One document's GetDocument call faults (ByggR SOAP 500). It must be recorded as BYGGR_FETCH_ERROR and must NOT
	// abort the batch - the other document still archives successfully.
	@Test
	void test15_byggrFetchError() throws JsonProcessingException, ClassNotFoundException {
		// Same fixed window as test14 so the batch loop runs only two passes (keeps the repeated fetch faults below the
		// arendeexport circuit breaker threshold).
		final var postBatchHistory = postBatchJob(BatchJob.builder()
			.withStart(LocalDate.parse("2021-12-17"))
			.withEnd(LocalDate.parse("2021-12-17"))
			.build());

		final var archiveHistories = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(postBatchHistory.getId(), MUNICIPALITY_ID);

		// The batch was not aborted: the other document archived OK, while the one whose fetch failed is not completed
		assertThat(archiveHistories)
			.anySatisfy(archiveHistory -> {
				assertThat(archiveHistory.getDocumentId()).isEqualTo("431169");
				assertThat(archiveHistory.getArchiveStatus()).isEqualTo(COMPLETED);
			})
			.anySatisfy(archiveHistory -> {
				assertThat(archiveHistory.getDocumentId()).isEqualTo("433467");
				assertThat(archiveHistory.getArchiveStatus()).isEqualTo(NOT_COMPLETED);
			});

		// The fetch failure was recorded as BYGGR_FETCH_ERROR
		final var archiveFailures = archiveFailureRepository.findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(postBatchHistory.getId(), MUNICIPALITY_ID, null);

		assertThat(archiveFailures)
			.isNotEmpty()
			.allSatisfy(archiveFailure -> {
				assertThat(archiveFailure.getDocumentId()).isEqualTo("433467");
				assertThat(archiveFailure.getFailureCategory()).isEqualTo(BYGGR_FETCH_ERROR);
			});
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
