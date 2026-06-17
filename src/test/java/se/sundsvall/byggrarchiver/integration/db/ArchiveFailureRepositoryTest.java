package se.sundsvall.byggrarchiver.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveFailure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.ARCHIVE_ERROR;
import static se.sundsvall.byggrarchiver.api.model.enums.FailureCategory.FILE_TOO_LARGE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
class ArchiveFailureRepositoryTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private ArchiveFailureRepository archiveFailureRepository;

	@Test
	void persistsAndFiltersByBatchMunicipalityAndOptionalCategory() {
		archiveFailureRepository.save(failure(1L, MUNICIPALITY_ID, ARCHIVE_ERROR, "case-1"));
		// caseId is null here - the insert must still succeed (no NOT NULL constraint)
		archiveFailureRepository.save(failure(1L, MUNICIPALITY_ID, FILE_TOO_LARGE, null));
		archiveFailureRepository.save(failure(2L, MUNICIPALITY_ID, ARCHIVE_ERROR, "case-other-batch"));
		archiveFailureRepository.save(failure(1L, "2282", ARCHIVE_ERROR, "case-other-muni"));

		// No category -> all failures for batch 1 / municipality 2281
		final var all = archiveFailureRepository.findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(1L, MUNICIPALITY_ID, null);
		assertThat(all)
			.hasSize(2)
			.extracting(ArchiveFailure::getFailureCategory)
			.containsExactlyInAnyOrder(ARCHIVE_ERROR, FILE_TOO_LARGE);
		assertThat(all).allSatisfy(failure -> {
			assertThat(failure.getId()).isNotNull();
			assertThat(failure.getTimestamp()).isNotNull();
		});

		// Category filter applied
		final var filtered = archiveFailureRepository.findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(1L, MUNICIPALITY_ID, ARCHIVE_ERROR);
		assertThat(filtered)
			.singleElement()
			.satisfies(failure -> {
				assertThat(failure.getFailureCategory()).isEqualTo(ARCHIVE_ERROR);
				assertThat(failure.getCaseId()).isEqualTo("case-1");
			});
	}

	private static ArchiveFailure failure(final Long batchHistoryId, final String municipalityId, final FailureCategory failureCategory, final String caseId) {
		return ArchiveFailure.builder()
			.withBatchHistoryId(batchHistoryId)
			.withMunicipalityId(municipalityId)
			.withFailureCategory(failureCategory)
			.withCaseId(caseId)
			.withDocumentId("doc")
			.withDocumentName("name")
			.withMessage("message")
			.withDetail("detail")
			.build();
	}

}
