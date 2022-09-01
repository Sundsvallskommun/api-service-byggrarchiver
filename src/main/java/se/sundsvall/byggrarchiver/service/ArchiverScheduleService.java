package se.sundsvall.byggrarchiver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ArchiverScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ArchiverScheduleService.class);

    private ByggrArchiverService byggrArchiverService;

    public ArchiverScheduleService (ByggrArchiverService byggrArchiverService) {
        this.byggrArchiverService = byggrArchiverService;
    }


    @Scheduled(cron = "${cron.expression}")
    public void archive() throws ApplicationException {
        log.info("Running archiving on schedule. Timestamp: {}", LocalDateTime.now());

        // Run batch from one week back in time to yesterday
        // TODO - change this when we run this job everyday
        LocalDate oneWeekBack = LocalDate.now().minusDays(7);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        byggrArchiverService.runBatch(oneWeekBack, yesterday, BatchTrigger.SCHEDULED);
    }
}