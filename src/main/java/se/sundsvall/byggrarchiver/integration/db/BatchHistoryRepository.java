package se.sundsvall.byggrarchiver.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

@Transactional
@CircuitBreaker(name = "batchHistoryRepository")
public interface BatchHistoryRepository extends JpaRepository<BatchHistory, Long> {

	List<BatchHistory> findAllByMunicipalityId(String municipalityId);

	Optional<BatchHistory> findByIdAndMunicipalityId(Long id, String municipalityId);

	List<BatchHistory> findBatchHistoriesByArchiveStatusAndMunicipalityId(ArchiveStatus archiveStatus, String municipalityId);

}
