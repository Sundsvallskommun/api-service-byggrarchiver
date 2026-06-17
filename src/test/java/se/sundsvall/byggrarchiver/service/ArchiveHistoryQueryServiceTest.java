package se.sundsvall.byggrarchiver.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
class ArchiveHistoryQueryServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private ArchiveHistoryRepository archiveHistoryRepositoryMock;

	@InjectMocks
	private ArchiveHistoryQueryService archiveHistoryQueryService;

	@Test
	void getArchiveHistoriesMapsResults() {
		final var entity = ArchiveHistory.builder()
			.withCaseId("caseId")
			.withDocumentId("documentId")
			.withArchiveStatus(COMPLETED)
			.build();

		when(archiveHistoryRepositoryMock.getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(COMPLETED, 1L, MUNICIPALITY_ID))
			.thenReturn(List.of(entity));

		final var result = archiveHistoryQueryService.getArchiveHistories(COMPLETED, 1L, MUNICIPALITY_ID);

		assertThat(result).singleElement().satisfies(response -> {
			assertThat(response.getCaseId()).isEqualTo("caseId");
			assertThat(response.getDocumentId()).isEqualTo("documentId");
			assertThat(response.getArchiveStatus()).isEqualTo(COMPLETED);
		});
		verify(archiveHistoryRepositoryMock).getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(COMPLETED, 1L, MUNICIPALITY_ID);
		verifyNoMoreInteractions(archiveHistoryRepositoryMock);
	}

	@Test
	void getArchiveHistoriesEmpty() {
		when(archiveHistoryRepositoryMock.getArchiveHistoriesByArchiveStatusAndBatchHistoryIdAndMunicipalityId(any(), any(), any()))
			.thenReturn(List.of());

		assertThat(archiveHistoryQueryService.getArchiveHistories(null, null, MUNICIPALITY_ID)).isEmpty();
	}

}
