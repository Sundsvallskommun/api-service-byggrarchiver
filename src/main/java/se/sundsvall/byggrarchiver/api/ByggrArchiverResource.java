package se.sundsvall.byggrarchiver.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.util.List;

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
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.byggrarchiver.api.model.ArchiveHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.BatchHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.api.validation.StartBeforeEnd;
import se.sundsvall.byggrarchiver.service.ArchiveHistoryService;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Validated
@RestController
@RequestMapping(path = "/{municipalityId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
@ApiResponse(responseCode = "200", description = "OK - Successful operation")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class ByggrArchiverResource {

	private final ByggrArchiverService byggrArchiverService;

	private final ArchiveHistoryService archiveHistoryService;

	ByggrArchiverResource(final ByggrArchiverService byggrArchiverService,

		final ArchiveHistoryService archiveHistoryService) {
		this.byggrArchiverService = byggrArchiverService;
		this.archiveHistoryService = archiveHistoryService;
	}

	@GetMapping("/archived/attachments")
	ResponseEntity<List<ArchiveHistoryResponse>> getArchiveHistory(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@RequestParam(value = "archiveStatus", required = false) final ArchiveStatus archiveStatus,
		@RequestParam(value = "batchHistoryId", required = false) final Long batchHistoryId) {
		return ResponseEntity.ok(archiveHistoryService.getArchiveHistories(archiveStatus, batchHistoryId, municipalityId));
	}

	@GetMapping("/batch-jobs")
	ResponseEntity<List<BatchHistoryResponse>> getBatchHistory(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId) {
		return ResponseEntity.ok(byggrArchiverService.findAllBatchHistories(municipalityId));
	}

	@PostMapping("/batch-jobs")
	public ResponseEntity<BatchHistoryResponse> postBatchJob(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Valid @StartBeforeEnd
		@NotNull(message = "Request body must not be null")
		@RequestBody final BatchJob batchJob) {
		return ResponseEntity.ok(byggrArchiverService.runBatch(batchJob.getStart(), batchJob.getEnd(), BatchTrigger.MANUAL, municipalityId));
	}

	@PostMapping("/batch-jobs/{batchHistoryId}/rerun")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<BatchHistoryResponse> reRunBatchJob(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@PathVariable("batchHistoryId") final Long batchHistoryId) {
		final var result = byggrArchiverService.reRunBatch(batchHistoryId, municipalityId);

		return ResponseEntity.ok((result));
	}

}
