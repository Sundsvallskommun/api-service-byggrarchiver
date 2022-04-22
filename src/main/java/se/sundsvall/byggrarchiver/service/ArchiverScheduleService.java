package se.sundsvall.byggrarchiver.service;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import org.jboss.logging.Logger;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.api.model.BatchTrigger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
public class ArchiverScheduleService {

    @Inject
    Logger log;

    @Inject
    ByggrArchiverService byggrArchiverService;


    @Scheduled(cron = "{cron.expression}")
    void archive(ScheduledExecution execution) throws ApplicationException {
        log.info("Running archiving on schedule. Timestamp: " + LocalDateTime.ofInstant(execution.getFireTime(), ZoneId.of("Europe/Stockholm")));

        // Run batch from one week back in time to today
        // TODO - change this when we run this job everyday
        LocalDate oneWeekBack = LocalDate.now().minusDays(7);
        LocalDate today = LocalDate.now();

        byggrArchiverService.runBatch(oneWeekBack, today, BatchTrigger.SCHEDULED);
    }
}