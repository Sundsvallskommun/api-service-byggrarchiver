package se.sundsvall.byggrarchiver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ArchiveScheduleServiceTest {

    @Mock
    private ByggrArchiverService byggrArchiverServiceMock;
    @InjectMocks
    private ArchiverScheduleService archiverScheduleService;

    @Test
    void archive() throws ApplicationException {
        doReturn(BatchHistory.builder().id(1L).build()).when(byggrArchiverServiceMock).runBatch(any(), any(), any());
        archiverScheduleService.archive();

        Mockito.verify(byggrArchiverServiceMock, times(1)).runBatch(LocalDate.now().minusDays(7), LocalDate.now().minusDays(1), BatchTrigger.SCHEDULED);
    }
}
