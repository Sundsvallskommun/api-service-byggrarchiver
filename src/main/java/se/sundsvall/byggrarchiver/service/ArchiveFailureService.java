package se.sundsvall.byggrarchiver.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.byggrarchiver.api.model.ArchiveFailureResponse;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;
import se.sundsvall.byggrarchiver.integration.db.ArchiveFailureRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveFailure;
import se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class ArchiveFailureService {

	private final ArchiveFailureRepository archiveFailureRepository;

	public ArchiveFailureService(final ArchiveFailureRepository archiveFailureRepository) {
		this.archiveFailureRepository = archiveFailureRepository;
	}

	/**
	 * Persists a single archive failure in its own transaction so the audit row survives even if the surrounding
	 * archiving work is rolled back. Invoked through {@link ArchiveFailureRecorder} which guarantees this never breaks
	 * the archiving pipeline.
	 */
	@Transactional(propagation = REQUIRES_NEW)
	public void persist(final ArchiveFailure archiveFailure) {
		archiveFailureRepository.save(archiveFailure);
	}

	public List<ArchiveFailureResponse> getFailures(final Long batchHistoryId, final FailureCategory failureCategory, final String municipalityId) {
		return archiveFailureRepository.findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(batchHistoryId, municipalityId, failureCategory)
			.stream()
			.map(ArchiverMapper::mapToArchiveFailureResponse)
			.toList();
	}

}
