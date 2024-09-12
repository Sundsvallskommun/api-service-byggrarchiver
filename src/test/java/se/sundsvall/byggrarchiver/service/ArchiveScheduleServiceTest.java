package se.sundsvall.byggrarchiver.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomLong;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

@ExtendWith(MockitoExtension.class)
class ArchiveScheduleServiceTest {

	@Mock
	private ByggrArchiverService mockByggrArchiverService;

	@InjectMocks
	private ArchiverScheduleService archiverScheduleService;

	@Test
	void archive() throws ApplicationException {
		when(mockByggrArchiverService.runBatch(any(LocalDate.class), any(LocalDate.class), any(BatchTrigger.class)))
			.thenReturn(BatchHistory.builder().withId(randomLong()).build());

		archiverScheduleService.archive();

		verify(mockByggrArchiverService, times(1)).runBatch(LocalDate.now().minusDays(7), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED);
		verifyNoMoreInteractions(mockByggrArchiverService);
	}

}
