package se.sundsvall.byggrarchiver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper;

/**
 * Best-effort recorder for the append-only archive failure audit log. Recording a failure must never break the
 * archiving pipeline, so every persist is wrapped and any error is swallowed (logged only). The persist itself runs in
 * a separate transaction (see {@link ArchiveFailureService#persist}); the cross-bean call ensures the
 * {@code REQUIRES_NEW} boundary is honored.
 */
@Component
public class ArchiveFailureRecorder {

	private static final Logger LOG = LoggerFactory.getLogger(ArchiveFailureRecorder.class);

	private final ArchiveFailureService archiveFailureService;

	public ArchiveFailureRecorder(final ArchiveFailureService archiveFailureService) {
		this.archiveFailureService = archiveFailureService;
	}

	/**
	 * Records a failure for the document the given {@link ArchiveHistory} represents. All identity (caseId, documentId,
	 * documentName, batchHistoryId, municipalityId) is taken from the entity, so callers only supply the category and a
	 * human-readable message + detail.
	 */
	public void recordFailure(final FailureCategory failureCategory, final ArchiveHistory archiveHistory, final String message, final String detail) {
		try {
			archiveFailureService.persist(ArchiverMapper.toArchiveFailure(failureCategory, archiveHistory, message, detail));
		} catch (final Exception e) {
			LOG.error("Failed to record archive failure (category={}, caseId={}, documentId={})", failureCategory, archiveHistory.getCaseId(), archiveHistory.getDocumentId(), e);
		}
	}

}
