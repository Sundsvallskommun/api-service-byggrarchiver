package se.sundsvall.byggrarchiver.service.scheduler;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
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

	private static final LocalDate TODAY = LocalDate.of(2024, Month.JANUARY, 16);
	private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private SchedulerProperties mockSchedulerProperties;

	@Mock
	private ByggrArchiverService mockByggrArchiverService;

	private ArchiverScheduler archiverScheduler;

	@BeforeEach
	void setup() {
		archiverScheduler = new ArchiverScheduler(mockByggrArchiverService, mockSchedulerProperties, CLOCK);
	}

	@Test
	void archive() {
		final var originalStart = TODAY.minusDays(7);
		final var end = TODAY.minusDays(1);
		final var batchTrigger = BatchTrigger.SCHEDULED;
		final var municipalityId = "2281";

		when(mockSchedulerProperties.municipalityIds()).thenReturn(List.of(municipalityId));

		when(mockByggrArchiverService.runBatch(originalStart, end, batchTrigger, municipalityId))
			.thenReturn(BatchHistoryResponse.builder().withId(randomLong()).build());

		archiverScheduler.archive();

		verify(mockByggrArchiverService).runBatch(originalStart, end, batchTrigger, municipalityId);
		verifyNoMoreInteractions(mockByggrArchiverService);
	}
}
