package se.sundsvall.byggrarchiver.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

import javax.transaction.Transactional;

@Transactional
public interface BatchHistoryRepository extends JpaRepository<BatchHistory, Long> {
}
