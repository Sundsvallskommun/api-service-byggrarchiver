package se.sundsvall.byggrarchiver.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface BatchHistoryRepository extends JpaRepository<BatchHistory, Long> {
    List<BatchHistory> findBatchHistoriesByArchiveStatus(ArchiveStatus archiveStatus);
}
