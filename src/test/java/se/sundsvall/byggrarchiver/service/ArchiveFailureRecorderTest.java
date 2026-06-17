package se.sundsvall.byggrarchiver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;

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

	@Test
	void recordDelegatesToService() {
		// Act
		archiveFailureRecorder.record(FailureCategory.FILE_TOO_LARGE, "caseId", "documentId", "documentName", 5L, "2281", "message", "detail");

		// Assert
		verify(archiveFailureServiceMock).persist(FailureCategory.FILE_TOO_LARGE, "caseId", "documentId", "documentName", 5L, "2281", "message", "detail");
	}

	@Test
	void recordNeverThrowsWhenServiceFails() {
		// Arrange
		doThrow(new RuntimeException("boom")).when(archiveFailureServiceMock)
			.persist(any(), any(), any(), any(), any(), any(), any(), any());

		// Act & Assert - recording a failure must never break the archiving pipeline
		assertThatCode(() -> archiveFailureRecorder.record(FailureCategory.ARCHIVE_ERROR, "caseId", "documentId", "documentName", 5L, "2281", "message", "detail"))
			.doesNotThrowAnyException();
	}

}
