package se.sundsvall.byggrarchiver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveFailure;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArchiveFailureRecorderTest {

	@Mock
	private ArchiveFailureService archiveFailureServiceMock;

	@InjectMocks
	private ArchiveFailureRecorder archiveFailureRecorder;

	private static ArchiveHistory archiveHistory() {
		return ArchiveHistory.builder()
			.withCaseId("caseId")
			.withDocumentId("documentId")
			.withDocumentName("documentName")
			.withMunicipalityId("2281")
			.withBatchHistory(BatchHistory.builder().withId(5L).build())
			.build();
	}

	@Test
	void recordFailureMapsArchiveHistoryAndDelegatesToService() {
		// Act
		archiveFailureRecorder.recordFailure(FailureCategory.FILE_TOO_LARGE, archiveHistory(), "message", "detail");

		// Assert - identity is pulled off the ArchiveHistory
		final var captor = ArgumentCaptor.forClass(ArchiveFailure.class);
		verify(archiveFailureServiceMock).persist(captor.capture());

		final var saved = captor.getValue();
		assertThat(saved.getFailureCategory()).isEqualTo(FailureCategory.FILE_TOO_LARGE);
		assertThat(saved.getCaseId()).isEqualTo("caseId");
		assertThat(saved.getDocumentId()).isEqualTo("documentId");
		assertThat(saved.getDocumentName()).isEqualTo("documentName");
		assertThat(saved.getBatchHistoryId()).isEqualTo(5L);
		assertThat(saved.getMunicipalityId()).isEqualTo("2281");
		assertThat(saved.getMessage()).isEqualTo("message");
		assertThat(saved.getDetail()).isEqualTo("detail");
	}

	@Test
	void recordFailureNeverThrowsWhenServiceFails() {
		// Arrange
		doThrow(new RuntimeException("boom")).when(archiveFailureServiceMock).persist(any());

		// Act & Assert - recording a failure must never break the archiving pipeline
		assertThatCode(() -> archiveFailureRecorder.recordFailure(FailureCategory.ARCHIVE_ERROR, archiveHistory(), "message", "detail"))
			.doesNotThrowAnyException();
	}

}
