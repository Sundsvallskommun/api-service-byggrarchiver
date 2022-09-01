package se.sundsvall.byggrarchiver.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ArchiveHistoryRepository extends JpaRepository<ArchiveHistory, Long> {

    ArchiveHistory getArchiveHistoryByDocumentIdAndCaseId(String documentId, String caseId);

    List<ArchiveHistory> getArchiveHistoriesByBatchHistoryId(Long batchHistoryId);

    @Query("select a from ArchiveHistory a where (:archiveStatus is null or a.archiveStatus = :archiveStatus) and (:batchHistoryId is null or a.batchHistory.id = :batchHistoryId) ")
    List<ArchiveHistory> getArchiveHistoriesByArchiveStatusAndBatchHistoryId(@Param("archiveStatus") ArchiveStatus archiveStatus, @Param("batchHistoryId") Long batchHistoryId);

}
