package se.sundsvall.byggrarchiver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;

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

	public void record(final FailureCategory failureCategory, final String caseId, final String documentId, final String documentName, final Long batchHistoryId, final String municipalityId, final String message, final String detail) {
		try {
			archiveFailureService.persist(failureCategory, caseId, documentId, documentName, batchHistoryId, municipalityId, message, detail);
		} catch (final Exception e) {
			LOG.error("Failed to record archive failure (category={}, caseId={}, documentId={})", failureCategory, caseId, documentId, e);
		}
	}

}
