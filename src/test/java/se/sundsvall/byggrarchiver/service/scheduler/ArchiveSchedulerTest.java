package se.sundsvall.byggrarchiver.service.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.byggrarchiver.api.model.BatchHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;

@ExtendWith(MockitoExtension.class)
class ArchiveSchedulerTest {

	@Mock
	private SchedulerProperties mockSchedulerProperties;

	@Mock
	private ByggrArchiverService mockByggrArchiverService;

	@InjectMocks
	private ArchiverScheduler archiverScheduler;

	@Test
	void archive() {

		// Arrange
		final var originalStart = LocalDate.now().minusDays(7);
		final var end = LocalDate.now().minusDays(1);
		final var batchTrigger = BatchTrigger.SCHEDULED;
		final var municipalityId = "2281";

		when(mockByggrArchiverService.runBatch(originalStart, end, batchTrigger, municipalityId))
			.thenReturn(BatchHistoryResponse.builder().withId(randomLong()).build());

		when(mockSchedulerProperties.municipalityIds()).thenReturn(List.of(municipalityId));

		//Act
		archiverScheduler.archive();

		// Assert
		verify(mockByggrArchiverService, times(1)).runBatch(originalStart, end, batchTrigger, municipalityId);
		verifyNoMoreInteractions(mockByggrArchiverService);
	}

}
