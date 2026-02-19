package se.sundsvall.byggrarchiver.service.scheduler;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.BatchHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

@ExtendWith(MockitoExtension.class)
class ArchiverSchedulerTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private SchedulerProperties mockSchedulerProperties;

	@Mock
	private ByggrArchiverService mockByggrArchiverService;

	@InjectMocks
	private ArchiverScheduler archiverScheduler;

	@Test
	void archive() {
		var originalStart = LocalDate.now().minusDays(7);
		var end = LocalDate.now().minusDays(1);
		var batchTrigger = BatchTrigger.SCHEDULED;
		var municipalityId = "2281";

		when(mockSchedulerProperties.municipalityIds()).thenReturn(List.of(municipalityId));

		when(mockByggrArchiverService.runBatch(originalStart, end, batchTrigger, municipalityId))
			.thenReturn(BatchHistoryResponse.builder().withId(randomLong()).build());

		archiverScheduler.archive();

		verify(mockByggrArchiverService).runBatch(originalStart, end, batchTrigger, municipalityId);
		verifyNoMoreInteractions(mockByggrArchiverService);
	}
}
