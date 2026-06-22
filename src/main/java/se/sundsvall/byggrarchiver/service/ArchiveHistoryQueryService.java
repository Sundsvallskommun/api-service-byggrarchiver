package se.sundsvall.byggrarchiver.service;

import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.api.model.ArchiveHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper;

@Service
public class ArchiveHistoryQueryService {

	private final ArchiveHistoryRepository archiveHistoryRepository;

	public ArchiveHistoryQueryService(final ArchiveHistoryRepository archiveHistoryRepository) {
		this.archiveHistoryRepository = archiveHistoryRepository;
	}

	public List<ArchiveHistoryResponse> getArchiveHistories(final ArchiveStatus archiveStatus, final Long batchHistoryId, final String municipalityId) {
		return archiveHistoryRepository.getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(archiveStatus, batchHistoryId, municipalityId)
			.stream()
			.map(ArchiverMapper::mapToArchiveHistoryResponse)
			.toList();
	}

}
