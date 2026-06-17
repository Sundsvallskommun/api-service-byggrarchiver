package se.sundsvall.byggrarchiver.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveFailure;

@Transactional
@CircuitBreaker(name = "archiveFailureRepository")
public interface ArchiveFailureRepository extends JpaRepository<ArchiveFailure, Long> {

	@Query("select f from ArchiveFailure f where f.batchHistoryId = :batchHistoryId and f.municipalityId = :municipalityId and (:failureCategory is null or f.failureCategory = :failureCategory)")
	List<ArchiveFailure> findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(@Param("batchHistoryId") Long batchHistoryId, @Param("municipalityId") String municipalityId, @Param("failureCategory") FailureCategory failureCategory);

}
