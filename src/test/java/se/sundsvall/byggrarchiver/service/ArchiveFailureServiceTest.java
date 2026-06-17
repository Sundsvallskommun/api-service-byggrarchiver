package se.sundsvall.byggrarchiver.service;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;
import se.sundsvall.byggrarchiver.integration.db.ArchiveFailureRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveFailure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArchiveFailureServiceTest {

	@Mock
	private ArchiveFailureRepository archiveFailureRepositoryMock;

	@InjectMocks
	private ArchiveFailureService archiveFailureService;

	@Test
	void persistSavesEntity() {
		// Arrange
		final var archiveFailure = ArchiveFailure.builder()
			.withFailureCategory(FailureCategory.ARCHIVE_ERROR)
			.withCaseId("caseId")
			.build();

		// Act
		archiveFailureService.persist(archiveFailure);

		// Assert
		verify(archiveFailureRepositoryMock).save(archiveFailure);
		verifyNoMoreInteractions(archiveFailureRepositoryMock);
	}

	@Test
	void getFailuresMapsResults() {
		// Arrange
		final var entity = ArchiveFailure.builder()
			.withId(1L)
			.withBatchHistoryId(5L)
			.withCaseId("caseId")
			.withDocumentId("documentId")
			.withMunicipalityId("2281")
			.withDocumentName("documentName")
			.withFailureCategory(FailureCategory.BYGGR_FETCH_ERROR)
			.withMessage("message")
			.withDetail("detail")
			.withTimestamp(LocalDateTime.now())
			.build();

		when(archiveFailureRepositoryMock.findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(5L, "2281", FailureCategory.BYGGR_FETCH_ERROR))
			.thenReturn(List.of(entity));

		// Act
		final var result = archiveFailureService.getFailures(5L, FailureCategory.BYGGR_FETCH_ERROR, "2281");

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(1L);
		assertThat(result.getFirst().getFailureCategory()).isEqualTo(FailureCategory.BYGGR_FETCH_ERROR);
		assertThat(result.getFirst().getCaseId()).isEqualTo("caseId");
		verify(archiveFailureRepositoryMock).findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(5L, "2281", FailureCategory.BYGGR_FETCH_ERROR);
		verifyNoMoreInteractions(archiveFailureRepositoryMock);
	}

	@Test
	void getFailuresPassesNullCategory() {
		// Arrange
		when(archiveFailureRepositoryMock.findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(any(), any(), any()))
			.thenReturn(List.of());

		// Act
		final var result = archiveFailureService.getFailures(5L, null, "2281");

		// Assert
		assertThat(result).isEmpty();
		verify(archiveFailureRepositoryMock).findByBatchHistoryIdAndMunicipalityIdAndOptionalFailureCategory(5L, "2281", null);
		verifyNoMoreInteractions(archiveFailureRepositoryMock);
	}

}
