package se.sundsvall.byggrarchiver.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;

@ExtendWith(MockitoExtension.class)
class BatchCompletionServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private BatchHistoryRepository batchHistoryRepositoryMock;

	@Mock
	private ArchiveHistoryRepository archiveHistoryRepositoryMock;

	@Mock
	private MessagingIntegration messagingIntegrationMock;

	@InjectMocks
	private BatchCompletionService batchCompletionService;

	private static ArchiveHistory history(final ArchiveStatus status) {
		return ArchiveHistory.builder().withArchiveStatus(status).build();
	}

	private static BatchHistory batch(final Long id) {
		return BatchHistory.builder().withId(id).withArchiveStatus(NOT_COMPLETED).build();
	}

	@Test
	void marksBatchCompletedWhenAllArchiveHistoriesCompleted() {
		final var currentBatch = batch(1L);
		when(archiveHistoryRepositoryMock.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(1L, MUNICIPALITY_ID))
			.thenReturn(List.of(history(COMPLETED), history(COMPLETED)));
		when(batchHistoryRepositoryMock.findBatchHistoriesByArchiveStatusAndMunicipalityId(NOT_COMPLETED, MUNICIPALITY_ID))
			.thenReturn(List.of());

		batchCompletionService.completeBatch(currentBatch, MUNICIPALITY_ID);

		assertThat(currentBatch.getArchiveStatus()).isEqualTo(COMPLETED);
		verify(batchHistoryRepositoryMock).save(currentBatch);
		verifyNoInteractions(messagingIntegrationMock);
	}

	@Test
	void sendsStatusMailWhenBatchNotCompletedAndDoesNotPromotePendingOldBatch() {
		final var currentBatch = batch(1L);
		final var oldBatch = batch(2L);
		final var histories = List.of(history(COMPLETED), history(NOT_COMPLETED));

		when(archiveHistoryRepositoryMock.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(1L, MUNICIPALITY_ID))
			.thenReturn(histories);
		when(batchHistoryRepositoryMock.findBatchHistoriesByArchiveStatusAndMunicipalityId(NOT_COMPLETED, MUNICIPALITY_ID))
			.thenReturn(List.of(oldBatch));
		when(archiveHistoryRepositoryMock.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(2L, MUNICIPALITY_ID))
			.thenReturn(List.of(history(NOT_COMPLETED)));

		batchCompletionService.completeBatch(currentBatch, MUNICIPALITY_ID);

		assertThat(currentBatch.getArchiveStatus()).isEqualTo(NOT_COMPLETED);
		assertThat(oldBatch.getArchiveStatus()).isEqualTo(NOT_COMPLETED);
		verify(messagingIntegrationMock).sendStatusMail(histories, 1L, MUNICIPALITY_ID);
		verify(batchHistoryRepositoryMock, never()).save(any());
	}

	@Test
	void promotesOldNotCompletedBatchWhoseDocumentsAreNowAllCompleted() {
		final var currentBatch = batch(1L);
		final var oldBatch = batch(2L);

		when(archiveHistoryRepositoryMock.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(1L, MUNICIPALITY_ID))
			.thenReturn(List.of(history(COMPLETED)));
		when(batchHistoryRepositoryMock.findBatchHistoriesByArchiveStatusAndMunicipalityId(NOT_COMPLETED, MUNICIPALITY_ID))
			.thenReturn(List.of(oldBatch));
		when(archiveHistoryRepositoryMock.getArchiveHistoriesByBatchHistoryIdAndMunicipalityId(2L, MUNICIPALITY_ID))
			.thenReturn(List.of(history(COMPLETED)));

		batchCompletionService.completeBatch(currentBatch, MUNICIPALITY_ID);

		assertThat(currentBatch.getArchiveStatus()).isEqualTo(COMPLETED);
		assertThat(oldBatch.getArchiveStatus()).isEqualTo(COMPLETED);
		verify(batchHistoryRepositoryMock).save(currentBatch);
		verify(batchHistoryRepositoryMock).save(oldBatch);
		verify(messagingIntegrationMock, never()).sendStatusMail(any(), eq(1L), eq(MUNICIPALITY_ID));
	}

}
