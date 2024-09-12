package se.sundsvall.byggrarchiver.integration.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;

@Transactional
public interface ArchiveHistoryRepository extends JpaRepository<ArchiveHistory, Long> {

	Optional<ArchiveHistory> getArchiveHistoryByDocumentIdAndCaseIdAndMunicipalityId(String documentId, String caseId, String municipalityId);

	List<ArchiveHistory> getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(Long batchHistoryId, String municipalityId);

	@Query("select a from ArchiveHistory a where (:archiveStatus is null or a.archiveStatus = :archiveStatus) and (:batchHistoryId is null or a.batchHistory.id = :batchHistoryId) and (:municipalityId is null or a.municipalityId = :municipalityId)")
	List<ArchiveHistory> getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(@Param("archiveStatus") ArchiveStatus archiveStatus, @Param("batchHistoryId") Long batchHistoryId, @Param("municipalityId") String municipalityId);

	void deleteArchiveHistoriesByCaseIdAndArchiveStatus(String caseId, ArchiveStatus archiveStatus);

}
