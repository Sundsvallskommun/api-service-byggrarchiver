package se.sundsvall.byggrarchiver.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.byggrarchiver.api.model.ArchiveHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.BatchHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.api.validation.StartBeforeEnd;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Validated
@RestController
@RequestMapping(produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
@ApiResponse(
	responseCode = "200",
	description = "OK - Successful operation"
)
@ApiResponse(
	responseCode = "400",
	description = "Bad request",
	content = @Content(
		mediaType = APPLICATION_PROBLEM_JSON_VALUE,
		schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})
	)
)
@ApiResponse(
	responseCode = "500",
	description = "Internal Server error",
	content = @Content(
		mediaType = APPLICATION_PROBLEM_JSON_VALUE,
		schema = @Schema(implementation = Problem.class)
	)
)
class ByggrArchiverResource {

	private final ByggrArchiverService byggrArchiverService;

	private final ArchiveHistoryRepository archiveHistoryRepository;

	private final BatchHistoryRepository batchHistoryRepository;

	ByggrArchiverResource(final ByggrArchiverService byggrArchiverService,
		final ArchiveHistoryRepository archiveHistoryRepository,
		final BatchHistoryRepository batchHistoryRepository) {
		this.byggrArchiverService = byggrArchiverService;
		this.archiveHistoryRepository = archiveHistoryRepository;
		this.batchHistoryRepository = batchHistoryRepository;
	}

	@GetMapping("/archived/attachments")
	@ApiResponse(
		responseCode = "404",
		description = "Not found",
		content = @Content(
			mediaType = APPLICATION_PROBLEM_JSON_VALUE,
			schema = @Schema(implementation = Problem.class)
		)
	)
	ResponseEntity<List<ArchiveHistoryResponse>> getArchiveHistory(
		@RequestParam(value = "archiveStatus", required = false) final ArchiveStatus archiveStatus,
		@RequestParam(value = "batchHistoryId", required = false) final Long batchHistoryId) {
		var archiveHistoryList = archiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryId(archiveStatus, batchHistoryId);

		if (archiveHistoryList.isEmpty()) {
			throw Problem.valueOf(Status.NOT_FOUND, "ArchiveHistory not found");
		}

		return ResponseEntity.ok(archiveHistoryList.stream()
			.map(this::mapToArchiveHistoryResponse)
			.toList());
	}

	@GetMapping("/batch-jobs")
	@ApiResponse(
		responseCode = "404",
		description = "Not found",
		content = @Content(
			mediaType = APPLICATION_PROBLEM_JSON_VALUE,
			schema = @Schema(implementation = Problem.class)
		)
	)
	ResponseEntity<List<BatchHistoryResponse>> getBatchHistory() {
		var batchHistoryList = batchHistoryRepository.findAll();

		if (batchHistoryList.isEmpty()) {
			throw Problem.valueOf(Status.NOT_FOUND, "BatchHistory not found");
		}

		return ResponseEntity.ok(batchHistoryList.stream()
			.map(this::mapToBatchHistoryResponse)
			.toList());
	}

	@PostMapping("/batch-jobs")
	public ResponseEntity<BatchHistoryResponse> postBatchJob(
		@Valid
		@StartBeforeEnd
		@NotNull(message = "Request body must not be null")
		@RequestBody final BatchJob batchJob) {
		var result = byggrArchiverService.runBatch(batchJob.getStart(), batchJob.getEnd(), BatchTrigger.MANUAL);

		return ResponseEntity.ok(mapToBatchHistoryResponse(result));
	}

	@PostMapping("/batch-jobs/{batchHistoryId}/rerun")
	@ApiResponse(
		responseCode = "404",
		description = "Not found",
		content = @Content(
			mediaType = APPLICATION_PROBLEM_JSON_VALUE,
			schema = @Schema(implementation = Problem.class)
		)
	)
	ResponseEntity<BatchHistoryResponse> reRunBatchJob(
		@PathVariable("batchHistoryId") final Long batchHistoryId) {
		var result = byggrArchiverService.reRunBatch(batchHistoryId);

		return ResponseEntity.ok(mapToBatchHistoryResponse(result));
	}

	ArchiveHistoryResponse mapToArchiveHistoryResponse(final ArchiveHistory archiveHistory) {
		if (archiveHistory == null) {
			return null;
		}

		return ArchiveHistoryResponse.builder()
			.withDocumentId(archiveHistory.getDocumentId())
			.withCaseId(archiveHistory.getCaseId())
			.withDocumentName(archiveHistory.getDocumentName())
			.withDocumentType(archiveHistory.getDocumentType())
			.withArchiveId(archiveHistory.getArchiveId())
			.withArchiveUrl(archiveHistory.getArchiveUrl())
			.withArchiveStatus(archiveHistory.getArchiveStatus())
			.withTimestamp(archiveHistory.getTimestamp())
			.withBatchHistory(Optional.ofNullable(archiveHistory.getBatchHistory())
				.map(this::mapToBatchHistoryResponse)
				.orElse(null))
			.build();
	}

	BatchHistoryResponse mapToBatchHistoryResponse(final BatchHistory batchHistory) {
		if (batchHistory == null) {
			return null;
		}

		return BatchHistoryResponse.builder()
			.withId(batchHistory.getId())
			.withStart(batchHistory.getStart())
			.withEnd(batchHistory.getEnd())
			.withArchiveStatus(batchHistory.getArchiveStatus())
			.withBatchTrigger(batchHistory.getBatchTrigger())
			.withTimestamp(batchHistory.getTimestamp())
			.build();
	}

}
