package se.sundsvall.byggrarchiver.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import se.sundsvall.byggrarchiver.api.model.BatchJob;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.api.validation.StartBeforeEnd;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.service.util.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@Validated
@RequestMapping("/")
public class ByggrArchiverResource {

    private final ByggrArchiverService byggrArchiverService;

    private final ArchiveHistoryRepository archiveHistoryRepository;

    private final BatchHistoryRepository batchHistoryRepository;

    public ByggrArchiverResource(ByggrArchiverService byggrArchiverService, ArchiveHistoryRepository archiveHistoryRepository, BatchHistoryRepository batchHistoryRepository) {
        this.byggrArchiverService = byggrArchiverService;
        this.archiveHistoryRepository = archiveHistoryRepository;
        this.batchHistoryRepository = batchHistoryRepository;
    }

    @GetMapping(path = "archived/attachments", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK - Successful operation")
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
    public ResponseEntity<List<ArchiveHistory>> getArchiveHistory(@RequestParam(value = "archiveStatus", required = false) ArchiveStatus archiveStatus, @RequestParam(value = "batchHistoryId", required = false) Long batchHistoryId) {
        List<ArchiveHistory> archiveHistoryList = archiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryId(archiveStatus, batchHistoryId);

        if (archiveHistoryList.isEmpty()) {
            throw Problem.valueOf(Status.NOT_FOUND, Constants.ARCHIVE_HISTORY_NOT_FOUND);
        } else {
            return ResponseEntity.ok(archiveHistoryList);
        }

    }

    @GetMapping(path = "batch-jobs", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK - Successful operation")
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
    public ResponseEntity<List<BatchHistory>> getBatchHistory() {
        List<BatchHistory> batchHistoryList = batchHistoryRepository.findAll();

        if (batchHistoryList.isEmpty()) {
            throw Problem.valueOf(Status.NOT_FOUND, Constants.BATCH_HISTORY_NOT_FOUND);
        } else {
            return ResponseEntity.ok(batchHistoryList);
        }
    }

    @PostMapping(path = "batch-jobs", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK - Successful operation")
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
    @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
    public ResponseEntity<BatchHistory> postBatchJob(@StartBeforeEnd @NotNull(message = "Request body must not be null") @Valid @RequestBody BatchJob batchJob) throws ApplicationException {
        return ResponseEntity.ok(byggrArchiverService.runBatch(batchJob.getStart(), batchJob.getEnd(), BatchTrigger.MANUAL));
    }

    @PostMapping(path = "batch-jobs/{batchHistoryId}/rerun", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK - Successful operation")
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
    public ResponseEntity<BatchHistory> reRunBatchJob(@PathVariable("batchHistoryId") Long batchHistoryId) throws ApplicationException {
        return ResponseEntity.ok(byggrArchiverService.reRunBatch(batchHistoryId));
    }
}
