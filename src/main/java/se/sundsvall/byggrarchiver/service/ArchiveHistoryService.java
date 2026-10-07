package se.sundsvall.byggrarchiver.service;

import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.HandelseHandling;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;
import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportIntegration;
import se.sundsvall.byggrarchiver.integration.arendeexport.DocumentTooLargeException;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED_FILE_TO_LARGE;
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.BYGGR_FETCH_ERROR;
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.FILE_TOO_LARGE;
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.METADATA_ERROR;
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.UNKNOWN;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.getAttachmentCategory;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toArchiveHistory;
import static se.sundsvall.byggrarchiver.util.Constants.BYGGR_HANDELSETYP_ARKIV;
import static se.sundsvall.byggrarchiver.util.Constants.BYGGR_STATUS_AVSLUTAT;

@Service
public class ArchiveHistoryService {

	private static final Logger LOG = LoggerFactory.getLogger(ArchiveHistoryService.class);

	private final ArchiveHistoryRepository archiveHistoryRepository;

	private final ArendeExportIntegration arendeExportIntegration;

	private final ArchiveAttachmentService archiveAttachmentService;
	private final BatchCompletionService batchCompletionService;
	private final LantmaterietNotifier lantmaterietNotifier;

	private final ArchiveFailureRecorder archiveFailureRecorder;

	private final Clock clock;

	private final Integer maximumFileSize;

	public ArchiveHistoryService(final ArendeExportIntegration arendeExportIntegration,
		final ArchiveHistoryRepository archiveHistoryRepository,
		final ArchiveAttachmentService archiveAttachmentService,
		final BatchCompletionService batchCompletionService,
		final LantmaterietNotifier lantmaterietNotifier,
		final ArchiveFailureRecorder archiveFailureRecorder,
		final Clock clock,
		@Value("${integration.archive.maximum-file-size}") final Integer maximumFileSize) {
		this.arendeExportIntegration = arendeExportIntegration;
		this.archiveHistoryRepository = archiveHistoryRepository;
		this.archiveAttachmentService = archiveAttachmentService;
		this.batchCompletionService = batchCompletionService;
		this.lantmaterietNotifier = lantmaterietNotifier;
		this.archiveFailureRecorder = archiveFailureRecorder;
		this.clock = clock;
		this.maximumFileSize = maximumFileSize;
	}

	public BatchHistory archive(final LocalDate searchStart, final LocalDate searchEnd,
		final BatchHistory batchHistory, final String municipalityId) {
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
			arendeBatch = arendeExportIntegration.getUpdatedArenden(batchFilter);

			final var closedCaseList = arendeBatch.getArenden().getArende().stream()
				.filter(arende -> BYGGR_STATUS_AVSLUTAT.equals(arende.getStatus()))
				.toList();

			closedCaseList.forEach(closedCase -> archiveCase(closedCase, batchHistory, municipalityId));
		} while (batchFilter.getLowerExclusiveBound().isBefore(end));

		return batchCompletionService.completeBatch(batchHistory, municipalityId);
	}

	/**
	 * Archives one closed case. Its stale rows are deleted right before its documents are processed, so a batch that is
	 * interrupted leaves every case either done or untouched.
	 */
	private void archiveCase(final Arende2 closedCase, final BatchHistory batchHistory, final String municipalityId) {
		// Delete all not completed archive histories connected to this case
		archiveHistoryRepository.deleteArchiveHistoriesByCaseIdAndArchiveStatus(closedCase.getDnr(), NOT_COMPLETED);

		// The case's archive histories are read once, not once per document
		final var archiveHistoryByDocumentId = archiveHistoryRepository.getArchiveHistoriesByCaseIdAndMunicipalityId(closedCase.getDnr(), municipalityId).stream()
			.collect(toMap(ArchiveHistory::getDocumentId, identity(), (first, second) -> first, HashMap::new));

		archiveHandlingar(closedCase).forEach(handling -> processHandlingList(handling, closedCase, batchHistory, municipalityId, archiveHistoryByDocumentId));
	}

	/**
	 * Retries the documents of a batch that are still NOT_COMPLETED. Each affected case is read with GetArende, so the
	 * batch's date window is not scanned again. NOT_COMPLETED_FILE_TO_LARGE documents are not retried.
	 */
	public BatchHistory rerun(final BatchHistory batchHistory, final String municipalityId) {
		final var notCompletedByCase = archiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(NOT_COMPLETED, batchHistory.getId(), municipalityId).stream()
			.collect(groupingBy(ArchiveHistory::getCaseId));

		LOG.info("Batch: {} is rerun for the NOT_COMPLETED documents in {} case(s)", batchHistory.getId(), notCompletedByCase.size());

		notCompletedByCase.forEach((caseId, notCompleted) -> rerunCase(caseId, notCompleted, batchHistory, municipalityId));

		return batchCompletionService.completeBatch(batchHistory, municipalityId);
	}

	private void rerunCase(final String caseId, final List<ArchiveHistory> notCompleted, final BatchHistory batchHistory, final String municipalityId) {
		final Arende2 arende;
		try {
			arende = arendeExportIntegration.getArende(caseId);
		} catch (final RuntimeException e) {
			LOG.error("Error when fetching Case-ID: {}, its documents stay NOT_COMPLETED", caseId, e);
			return;
		}

		if ((arende == null) || !BYGGR_STATUS_AVSLUTAT.equals(arende.getStatus())) {
			LOG.warn("Case-ID: {} is not closed in ByggR, its documents stay NOT_COMPLETED", caseId);
			return;
		}

		final var documentIds = notCompleted.stream()
			.map(ArchiveHistory::getDocumentId)
			.collect(toSet());
		final var handlingar = archiveHandlingar(arende)
			.filter(handling -> documentIds.contains(handling.getDokument().getDokId()))
			.toList();

		// Same as in archive(): a document that is no longer in an archive event of the closed case is dropped
		final var droppedIds = new HashSet<>(documentIds);
		handlingar.forEach(handling -> droppedIds.remove(handling.getDokument().getDokId()));
		if (!droppedIds.isEmpty()) {
			LOG.warn("Document-IDs: {} are no longer in an archive event of Case-ID: {} and are removed from the batch", droppedIds, caseId);
		}

		archiveHistoryRepository.deleteAll(notCompleted);

		// None of the retried documents has a row any more
		final var archiveHistoryByDocumentId = new HashMap<String, ArchiveHistory>();
		handlingar.forEach(handling -> processHandlingList(handling, arende, batchHistory, municipalityId, archiveHistoryByDocumentId));
	}

	private static Stream<HandelseHandling> archiveHandlingar(final Arende2 arende) {
		return arende.getHandelseLista().getHandelse().stream()
			.filter(handelse -> BYGGR_HANDELSETYP_ARKIV.equals(handelse.getHandelsetyp()))
			.flatMap(handelse -> handelse.getHandlingLista().getHandling().stream())
			.filter(handelseHandling -> handelseHandling.getDokument() != null);
	}

	private LocalDateTime getEnd(final LocalDate searchEnd) {
		final var now = LocalDateTime.now(clock);
		if (searchEnd.isBefore(now.toLocalDate())) {
			return searchEnd.atTime(23, 59, 59);
		}

		return now;
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

	private static FailureCategory categoryForApplicationException(final ApplicationException e) {
		return ofNullable(e.getMessage())
			.filter(message -> message.contains("marshal"))
			.map(message -> METADATA_ERROR)
			.orElse(UNKNOWN);
	}

	/**
	 * @param archiveHistoryByDocumentId the case's archive histories by document id. The new archive history is added to
	 *                                   it,
	 *                                   since a document can occur more than once in the case's archive events.
	 */
	private void processHandlingList(final HandelseHandling handling, final Arende2 arende, final BatchHistory batchHistory, final String municipalityId,
		final Map<String, ArchiveHistory> archiveHistoryByDocumentId) {
		final ArchiveHistory newArchiveHistory;
		final var docId = handling.getDokument().getDokId();
		final var oldArchiveHistory = archiveHistoryByDocumentId.get(docId);

		if (oldArchiveHistory != null) {
			LOG.info("Document-ID: {} in combination with Case-ID: {} already has archive status {}.", docId, arende.getDnr(), oldArchiveHistory.getArchiveStatus());
			return;
		}
		LOG.info("Document-ID: {} in combination with Case-ID: {} does not exist in the db. Archive it..", docId, arende.getDnr());
		newArchiveHistory = toArchiveHistory(handling, batchHistory, arende.getDnr(), getAttachmentCategory(handling.getTyp()), NOT_COMPLETED, municipalityId);
		archiveHistoryRepository.save(newArchiveHistory);
		archiveHistoryByDocumentId.put(docId, newArchiveHistory);
		// Get documents from Byggr
		final List<Dokument> dokumentList;
		try {
			dokumentList = arendeExportIntegration.getDocument(docId);
		} catch (final DocumentTooLargeException e) {
			// Rejected before the response was read into memory, see SOAPJAXBDecoder
			LOG.info("Document-ID: {} is too large to be fetched from ByggR ({}). Setting archive history status to {}", docId, e.getMessage(), NOT_COMPLETED_FILE_TO_LARGE);
			setFileTooLarge(newArchiveHistory, e.getMessage());
			return;
		} catch (final RuntimeException e) {
			// A failed document fetch must not abort the whole batch - record it and continue with the next document.
			// Catches both the Problem thrown on a SOAP fault and any other runtime failure from the Feign call
			// (e.g. CircuitBreaker CallNotPermittedException when the arendeexport breaker is open).
			LOG.error("Error when fetching document with ID: {} in combination with Case-ID: {}", docId, arende.getDnr(), e);
			archiveFailureRecorder.recordFailure(BYGGR_FETCH_ERROR, newArchiveHistory, "ByggR getDocument failed", e.getMessage());
			return;
		}

		// Archive documents - a failure for one document must not abort the rest of the batch
		try {
			handleArchiving(dokumentList, arende, handling, newArchiveHistory, municipalityId);
		} catch (final ApplicationException e) {
			LOG.error("Error when archiving document with ID: {} in combination with Case-ID: {}", docId, arende.getDnr(), e);
			archiveFailureRecorder.recordFailure(categoryForApplicationException(e), newArchiveHistory, "Archiving failed", e.getMessage());
		}
	}

	void handleArchiving(final List<Dokument> dokuments, final Arende2 arende, final HandelseHandling handling, final ArchiveHistory archiveHistory, final String municipalityId) throws ApplicationException {
		for (final var dokument : dokuments) {
			if (dokument.getFil().getFilBuffer().length > maximumFileSize) {
				LOG.info("Document-ID: {} is too large ({} bytes) to be archived, maximum file size is set to {} bytes. Setting archive history status to {}", dokument.getDokId(), dokument.getFil().getFilBuffer().length, maximumFileSize,
					NOT_COMPLETED_FILE_TO_LARGE);
				setFileTooLarge(archiveHistory, "actual=" + dokument.getFil().getFilBuffer().length + " bytes, max=" + maximumFileSize + " bytes");
				continue;
			}

			LOG.info("Case-ID: {} Document name: {} Handlingstyp: {} Handling-ID: {} Document-ID: {}",
				arende.getDnr(), dokument.getNamn(), handling.getTyp(),
				handling.getHandlingId(), dokument.getDokId());

			final var savedArchiveHistory = archiveAttachmentService.archiveAttachment(arende, handling, dokument, archiveHistory, municipalityId);

			lantmaterietNotifier.notifyIfGeoDocument(arende, handling, savedArchiveHistory, municipalityId);
		}
	}

	private void setFileTooLarge(final ArchiveHistory archiveHistory, final String detail) {
		archiveHistory.setArchiveStatus(NOT_COMPLETED_FILE_TO_LARGE);
		archiveHistoryRepository.save(archiveHistory);
		archiveFailureRecorder.recordFailure(FILE_TOO_LARGE, archiveHistory, "File too large", detail);
	}

}
