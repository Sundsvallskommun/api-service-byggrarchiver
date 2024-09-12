package se.sundsvall.byggrarchiver.service;

import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.GEO;
import static se.sundsvall.byggrarchiver.service.Constants.BYGGR_HANDELSETYP_ARKIV;
import static se.sundsvall.byggrarchiver.service.Constants.BYGGR_STATUS_AVSLUTAT;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toArchiveHistory;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toArendeFastighetList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.HandelseHandling;

@Service
public class ArchiveHistoryService {

	private static final Logger LOG = LoggerFactory.getLogger(ArchiveHistoryService.class);

	private final BatchHistoryRepository batchHistoryRepository;

	private final ArendeExportIntegrationService arendeExportIntegrationService;

	private final ArchiveHistoryRepository archiveHistoryRepository;

	private final MessagingIntegration messagingIntegration;

	private final ArchiveAttachmentService archiveAttachmentService;

	private final FastighetService fastighetService;

	public ArchiveHistoryService(final BatchHistoryRepository batchHistoryRepository,
		final ArendeExportIntegrationService arendeExportIntegrationService,
		final ArchiveHistoryRepository archiveHistoryRepository,
		final MessagingIntegration messagingIntegration,
		final ArchiveAttachmentService archiveAttachmentService,
		final FastighetService fastighetService) {
		this.batchHistoryRepository = batchHistoryRepository;
		this.arendeExportIntegrationService = arendeExportIntegrationService;
		this.archiveHistoryRepository = archiveHistoryRepository;
		this.messagingIntegration = messagingIntegration;
		this.archiveAttachmentService = archiveAttachmentService;
		this.fastighetService = fastighetService;
	}

	public BatchHistory archive(final LocalDate searchStart, final LocalDate searchEnd,
		final BatchHistory batchHistory) {
		LOG.info("Batch: {} was started with start-date: {} and end-date: {}", batchHistory.getId(), searchStart, searchEnd);

		final var start = searchStart.atStartOfDay();
		final var end = getEnd(searchEnd);
		final var batchFilter = new BatchFilter()
			.withLowerExclusiveBound(start)
			.withUpperInclusiveBound(end);

		ArendeBatch arendeBatch = null;

		do {
			if (arendeBatch != null) {
				setLowerExclusiveBoundWithReturnedValue(batchFilter, arendeBatch);
			}

			LOG.info("Run batch iteration with start-date: {} and end-date: {}", batchFilter.getLowerExclusiveBound(), batchFilter.getUpperInclusiveBound());

			// Get arenden from Byggr
			arendeBatch = arendeExportIntegrationService.getUpdatedArenden(batchFilter);

			final var closedCaseList = arendeBatch.getArenden().getArende().stream()
				.filter(arende -> BYGGR_STATUS_AVSLUTAT.equals(arende.getStatus()))
				.toList();

			// Delete all not completed archive histories connected to this case
			closedCaseList.forEach(closedCase -> archiveHistoryRepository.deleteArchiveHistoriesByCaseIdAndArchiveStatus(closedCase.getDnr(), NOT_COMPLETED));

			// Archive documents
			closedCaseList.forEach(closedCase -> closedCase.getHandelseLista().getHandelse().stream()
				.filter(handelse -> BYGGR_HANDELSETYP_ARKIV.equals(handelse.getHandelsetyp()))
				.flatMap(handelse -> handelse.getHandlingLista().getHandling().stream())
				.filter(handelseHandling -> handelseHandling.getDokument() != null)
				.forEach(handling -> {
					try {
						processHandlingList(handling, closedCase, batchHistory);
					} catch (final ApplicationException e) {
						LOG.error("Error when archiving document with ID: {} in combination with Case-ID: {}", handling.getDokument().getDokId(), closedCase.getDnr(), e);
					}
				}));
		} while (batchFilter.getLowerExclusiveBound().isBefore(end));

		final var archiveHistoriesRelatedToBatch = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryId(batchHistory.getId());
		if (archiveHistoriesRelatedToBatch.stream().allMatch(archiveHistory -> COMPLETED.equals(archiveHistory.getArchiveStatus()))) {
			// Persist that this batch is completed
			batchHistory.setArchiveStatus(COMPLETED);
			batchHistoryRepository.save(batchHistory);
		} else {
			// Send email when batch is not completed
			messagingIntegration.sendStatusMail(archiveHistoriesRelatedToBatch, batchHistory.getId());
		}

		LOG.info("Batch with ID: {} is {}", batchHistory.getId(), batchHistory.getArchiveStatus());
		LOG.info("Batch with ID: {} has {} archive histories", batchHistory.getId(), archiveHistoriesRelatedToBatch.size());

		updateStatusOfOldBatchHistories();

		return batchHistory;
	}

	/**
	 * Update the status of NOT_COMPLETED old batch histories to COMPLETED if all archive histories are COMPLETED
	 */
	private void updateStatusOfOldBatchHistories() {
		final var notCompletedBatchHistories = batchHistoryRepository.findBatchHistoriesByArchiveStatus(NOT_COMPLETED);

		notCompletedBatchHistories.forEach(batchHistory -> {
			final boolean allCompleted = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryId(batchHistory.getId())
				.stream()
				.allMatch(archiveHistory -> COMPLETED.equals(archiveHistory.getArchiveStatus()));

			if (allCompleted) {
				batchHistory.setArchiveStatus(COMPLETED);
				batchHistoryRepository.save(batchHistory);

				LOG.info("Old batch with ID: {} was NOT_COMPLETED but is now COMPLETED", batchHistory.getId());
			}
		});
	}

	private LocalDateTime getEnd(final LocalDate searchEnd) {
		final var now = LocalDateTime.now(ZoneId.systemDefault());
		if (searchEnd.isBefore(now.toLocalDate())) {
			return searchEnd.atTime(23, 59, 59);
		}

		return LocalDateTime.now(ZoneId.systemDefault());
	}

	private void setLowerExclusiveBoundWithReturnedValue(final BatchFilter filter, final ArendeBatch arendeBatch) {
		LOG.info("Last ArendeBatch start: {} end: {}", arendeBatch.getBatchStart(), arendeBatch.getBatchEnd());

		if ((arendeBatch.getBatchEnd() == null)
			|| arendeBatch.getBatchEnd().isEqual(filter.getLowerExclusiveBound())
			|| arendeBatch.getBatchEnd().isBefore(filter.getLowerExclusiveBound())) {
			final var plusOneHour = filter.getLowerExclusiveBound().plusHours(1);
			filter.setLowerExclusiveBound(plusOneHour.isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : plusOneHour);
		} else {
			filter.setLowerExclusiveBound(arendeBatch.getBatchEnd().isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : arendeBatch.getBatchEnd());
		}
	}

	private AttachmentCategory getAttachmentCategory(final String handlingsTyp) {
		try {
			return AttachmentCategory.fromCode(handlingsTyp);
		} catch (final IllegalArgumentException e) {
			// All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL, which
			// means they get the archiveClassification D, which means that they are not public in
			// the archive
			return AttachmentCategory.BIL;
		}
	}

	private void processHandlingList(HandelseHandling handling, Arende2 arende, BatchHistory batchHistory) throws ApplicationException {
		ArchiveHistory newArchiveHistory;
		final var docId = handling.getDokument().getDokId();
		final var oldArchiveHistory = archiveHistoryRepository.getArchiveHistoryByDocumentIdAndCaseId(docId, arende.getDnr());

		if (oldArchiveHistory.isPresent()) {
			LOG.info("Document-ID: {} in combination with Case-ID: {} is already archived.", docId, arende.getDnr());
			return;
		}
		LOG.info("Document-ID: {} in combination with Case-ID: {} does not exist in the db. Archive it..", docId, arende.getDnr());
		newArchiveHistory = toArchiveHistory(handling, batchHistory, arende.getDnr(), getAttachmentCategory(handling.getTyp()), NOT_COMPLETED);
		archiveHistoryRepository.save(newArchiveHistory);
		// Get documents from Byggr
		final var dokumentList = arendeExportIntegrationService.getDocument(docId);

		// Archive documents
		handleArchiving(dokumentList, arende, handling, newArchiveHistory);
	}

	private void handleArchiving(List<generated.se.sundsvall.arendeexport.Dokument> dokumentList, Arende2 arende, HandelseHandling handling, ArchiveHistory archiveHistory) throws ApplicationException {

		if (isArchived(archiveHistory)) {
			LOG.info("ArchiveHistory already got a archive-ID. Set status to {}", COMPLETED);

			archiveHistory.setArchiveStatus(COMPLETED);
			archiveHistoryRepository.save(archiveHistory);
			return;
		}

		for (final var dokument : dokumentList) {
			LOG.info("Case-ID: {} Document name: {} Handlingstyp: {} Handling-ID: {} Document-ID: {}",
				arende.getDnr(), dokument.getNamn(), handling.getTyp(),
				handling.getHandlingId(), dokument.getDokId());

			final var savedArchiveHistory = archiveAttachmentService.archiveAttachment(arende, handling, dokument, archiveHistory);

			if (COMPLETED.equals(savedArchiveHistory.getArchiveStatus())
				&& (savedArchiveHistory.getArchiveId() != null)
				&& GEO.equals(getAttachmentCategory(handling.getTyp()))) {
				// Send email to Lantmateriet with info about the archived attachment
				final var arendeFastighetList = toArendeFastighetList(arende.getObjektLista().getAbstractArendeObjekt());

				messagingIntegration.sendEmailToLantmateriet(
					fastighetService.getFastighet(arendeFastighetList).getFastighetsbeteckning(), savedArchiveHistory);
			}
		}
	}

	private boolean isArchived(final ArchiveHistory archiveHistory) {
		return (archiveHistory != null) && (archiveHistory.getArchiveId() != null);
	}

}
