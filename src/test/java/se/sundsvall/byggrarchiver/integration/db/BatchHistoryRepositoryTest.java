package se.sundsvall.byggrarchiver.integration.db;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED_FILE_TO_LARGE;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.SCHEDULED;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
class BatchHistoryRepositoryTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private BatchHistoryRepository batchHistoryRepository;

	@Autowired
	private ArchiveHistoryRepository archiveHistoryRepository;

	@Test
	void findNotCompletedBatchHistoriesWithAllArchiveHistoriesCompleted() {
		final var allCompleted = batch(NOT_COMPLETED, MUNICIPALITY_ID, COMPLETED, COMPLETED);
		batch(NOT_COMPLETED, MUNICIPALITY_ID, COMPLETED, NOT_COMPLETED);
		batch(NOT_COMPLETED, MUNICIPALITY_ID, COMPLETED, NOT_COMPLETED_FILE_TO_LARGE);
		batch(COMPLETED, MUNICIPALITY_ID, COMPLETED);
		batch(NOT_COMPLETED, "2282", COMPLETED);

		assertThat(batchHistoryRepository.findNotCompletedBatchHistoriesWithAllArchiveHistoriesCompleted(MUNICIPALITY_ID))
			.extracting(BatchHistory::getId)
			.containsExactly(allCompleted.getId());
	}

	private BatchHistory batch(final ArchiveStatus batchStatus, final String municipalityId, final ArchiveStatus... documentStatuses) {
		final var batchHistory = batchHistoryRepository.save(BatchHistory.builder()
			.withMunicipalityId(municipalityId)
			.withStart(LocalDate.of(2026, Month.SEPTEMBER, 1))
			.withEnd(LocalDate.of(2026, Month.SEPTEMBER, 7))
			.withArchiveStatus(batchStatus)
			.withBatchTrigger(SCHEDULED)
			.build());

		for (final var documentStatus : documentStatuses) {
			archiveHistoryRepository.save(ArchiveHistory.builder()
				.withDocumentId(UUID.randomUUID().toString())
				.withCaseId("case-" + batchHistory.getId())
				.withMunicipalityId(municipalityId)
				.withArchiveStatus(documentStatus)
				.withBatchHistory(batchHistory)
				.build());
		}

		return batchHistory;
	}

}
