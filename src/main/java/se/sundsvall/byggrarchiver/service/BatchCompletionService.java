package se.sundsvall.byggrarchiver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;

import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;

@Service
public class BatchCompletionService {

	private static final Logger LOG = LoggerFactory.getLogger(BatchCompletionService.class);

	private final BatchHistoryRepository batchHistoryRepository;
	private final ArchiveHistoryRepository archiveHistoryRepository;
	private final MessagingIntegration messagingIntegration;

	public BatchCompletionService(final BatchHistoryRepository batchHistoryRepository, final ArchiveHistoryRepository archiveHistoryRepository, final MessagingIntegration messagingIntegration) {
		this.batchHistoryRepository = batchHistoryRepository;
		this.archiveHistoryRepository = archiveHistoryRepository;
		this.messagingIntegration = messagingIntegration;
	}

	/**
	 * Closes out a finished batch: marks it COMPLETED when all its archive histories are completed, otherwise sends a
	 * status mail. Also retroactively promotes older NOT_COMPLETED batches whose documents have all since completed.
	 */
	public void completeBatch(final BatchHistory batchHistory, final String municipalityId) {
		final var archiveHistoriesRelatedToBatch = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(batchHistory.getId(), municipalityId);
		if (archiveHistoriesRelatedToBatch.stream().allMatch(archiveHistory -> COMPLETED.equals(archiveHistory.getArchiveStatus()))) {
			// Persist that this batch is completed
			batchHistory.setArchiveStatus(COMPLETED);
			batchHistoryRepository.save(batchHistory);
		} else {
			// Send email when batch is not completed
			messagingIntegration.sendStatusMail(archiveHistoriesRelatedToBatch, batchHistory.getId(), municipalityId);
		}

		LOG.info("Batch with ID: {} is {}", batchHistory.getId(), batchHistory.getArchiveStatus());
		LOG.info("Batch with ID: {} has {} archive histories", batchHistory.getId(), archiveHistoriesRelatedToBatch.size());

		updateStatusOfOldBatchHistories(municipalityId);
	}

	/**
	 * Update the status of NOT_COMPLETED old batch histories to COMPLETED if all archive histories are COMPLETED
	 */
	private void updateStatusOfOldBatchHistories(final String municipalityId) {
		final var notCompletedBatchHistories = batchHistoryRepository.findBatchHistoriesByArchiveStatusAndMunicipalityId(NOT_COMPLETED, municipalityId);

		notCompletedBatchHistories.forEach(batchHistory -> {
			final boolean allCompleted = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(batchHistory.getId(), municipalityId)
				.stream()
				.allMatch(archiveHistory -> COMPLETED.equals(archiveHistory.getArchiveStatus()));

			if (allCompleted) {
				batchHistory.setArchiveStatus(COMPLETED);
				batchHistoryRepository.save(batchHistory);

				LOG.info("Old batch with ID: {} was NOT_COMPLETED but is now COMPLETED", batchHistory.getId());
			}
		});
	}

}
