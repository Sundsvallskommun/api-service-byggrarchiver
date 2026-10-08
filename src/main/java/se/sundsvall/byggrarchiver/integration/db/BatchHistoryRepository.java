package se.sundsvall.byggrarchiver.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

@Transactional
@CircuitBreaker(name = "batchHistoryRepository")
public interface BatchHistoryRepository extends JpaRepository<BatchHistory, Long> {

	List<BatchHistory> findAllByMunicipalityId(String municipalityId);

	Optional<BatchHistory> findByIdAndMunicipalityId(Long id, String municipalityId);

	List<BatchHistory> findBatchHistoriesByArchiveStatusAndMunicipalityId(ArchiveStatus archiveStatus, String municipalityId);

	/**
	 * NOT_COMPLETED batches whose archive histories have all completed since. One query, instead of loading every archive
	 * history of every NOT_COMPLETED batch.
	 */
	@Query("""
		select b from BatchHistory b
		where b.archiveStatus = se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED
		and b.municipalityId = :municipalityId
		and not exists (
		select a from ArchiveHistory a
		where a.batchHistory = b
		and a.municipalityId = :municipalityId
		and a.archiveStatus <> se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED)
		""")
	List<BatchHistory> findNotCompletedBatchHistoriesWithAllArchiveHistoriesCompleted(@Param("municipalityId") String municipalityId);

}
