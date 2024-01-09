package se.sundsvall.byggrarchiver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

import java.time.LocalDate;
import java.util.Comparator;

import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;

@Service
public class ByggrArchiverService {

    private static final Logger LOG = LoggerFactory.getLogger(ByggrArchiverService.class);
    private final BatchHistoryRepository batchHistoryRepository;
    private final ArchiveHistoryService archiveHistoryService;

    public ByggrArchiverService(final BatchHistoryRepository batchHistoryRepository,
            final ArchiveHistoryService archiveHistoryService) {
        this.batchHistoryRepository = batchHistoryRepository;
        this.archiveHistoryService = archiveHistoryService;
    }

    public BatchHistory runBatch(final LocalDate originalStart, final LocalDate end,
            final BatchTrigger batchTrigger) {
        LOG.info("Batch with BatchTrigger: {} was started with start: {} and end: {}", batchTrigger, originalStart, end);

        var actualStart = originalStart;

        if (batchTrigger.equals(BatchTrigger.SCHEDULED)) {
            actualStart = getBatchStartOfScheduledJob(originalStart, end);
        }
        // If actualStart is null, we don't need to run the batch again and we return null.
        BatchHistory result = null;
        if (actualStart != null) {
            // Persist the start of this batch
            var batchHistory = createBatchHistory(actualStart, end, batchTrigger, NOT_COMPLETED);
            batchHistoryRepository.save(batchHistory);
            // Do the archiving
            result = archiveHistoryService.archive(actualStart, end, batchHistory);
        }

        return result;
    }

    public BatchHistory reRunBatch(final Long batchHistoryId) {
        LOG.info("Rerun was started with batchHistoryId: {}", batchHistoryId);

        var batchHistory = batchHistoryRepository.findById(batchHistoryId)
            .orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND, "BatchHistory not found"));

        if (batchHistory.getArchiveStatus().equals(COMPLETED)) {
            throw Problem.valueOf(Status.BAD_REQUEST, "It's not possible to rerun a completed batch.");
        }

        LOG.info("Rerun batch: {}", batchHistory);

        // Do the archiving
        return archiveHistoryService.archive(batchHistory.getStart(), batchHistory.getEnd(), batchHistory);
    }

    private BatchHistory getLatestCompletedBatch() {
        var batchHistoryList = batchHistoryRepository.findAll();

        // Filter completed batches
        batchHistoryList = batchHistoryList.stream()
            .filter(b -> b.getArchiveStatus().equals(COMPLETED))
            .toList();

        // Sort by end-date of batch
        batchHistoryList = batchHistoryList.stream()
            .sorted(Comparator.comparing(BatchHistory::getEnd, Comparator.reverseOrder()))
            .toList();

        // Get the latest batch
        return batchHistoryList.stream()
            .findFirst()
            .map(latestBatch -> {
                LOG.info("The latest batch: {}", latestBatch);

                return latestBatch;
            })
            .orElse(null);
    }

    private BatchHistory createBatchHistory(LocalDate actualStart, LocalDate end, BatchTrigger batchTrigger, ArchiveStatus archiveStatus) {
        return BatchHistory.builder()
            .withStart(actualStart)
            .withEnd(end)
            .withBatchTrigger(batchTrigger)
            .withArchiveStatus(archiveStatus).build();
    }

    private LocalDate getBatchStartOfScheduledJob(LocalDate start, LocalDate end) {
        var latestBatch = getLatestCompletedBatch();

        if (latestBatch != null) {
            // If this batch end-date is not after the latest batch end date, we don't need to run it again
            if (!end.isAfter(latestBatch.getEnd())) {
                LOG.info("This batch does not have a later end-date({}) than the latest batch ({}). Cancelling this batch...", end, latestBatch.getEnd());
                return null;
            }

            // If there is a gap between the latest batch end-date and this batch start-date, we would risk to miss something.
            // Therefore - set the start-date to the latest batch end-date, plus one day.
            var dayAfterLatestBatch = latestBatch.getEnd().plusDays(1);
            if (start.isAfter(dayAfterLatestBatch)) {
                LOG.info("It was a gap between the latest batch end-date and this batch start-date. Sets the start-date to: {}", latestBatch.getEnd().plusDays(1));
                start = dayAfterLatestBatch;
            }
        }
        return start;
    }
}
