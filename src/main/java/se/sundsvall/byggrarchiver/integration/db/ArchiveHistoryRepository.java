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

	Optional<ArchiveHistory> getArchiveHistoryByDocumentIdAndCaseId(String documentId, String caseId);

	List<ArchiveHistory> getArchiveHistoriesByBatchHistoryId(Long batchHistoryId);

	@Query("select a from ArchiveHistory a where (:archiveStatus is null or a.archiveStatus = :archiveStatus) and (:batchHistoryId is null or a.batchHistory.id = :batchHistoryId) ")
	List<ArchiveHistory> getArchiveHistoriesByArchiveStatusAndBatchHistoryId(@Param("archiveStatus") ArchiveStatus archiveStatus, @Param("batchHistoryId") Long batchHistoryId);

	void deleteArchiveHistoriesByCaseIdAndArchiveStatus(String caseId, ArchiveStatus archiveStatus);

}
