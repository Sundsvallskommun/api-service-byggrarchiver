package se.sundsvall;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import org.jboss.logging.Logger;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.vo.BatchTrigger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
public class ArchiverSchedule {

    @Inject
    Logger log;

    @Inject
    Archiver archiver;


    @Scheduled(cron = "{cron.expression}")
    void archive(ScheduledExecution execution) throws ApplicationException, JsonProcessingException {
        log.info("Archiving... Timestamp: " + LocalDateTime.ofInstant(execution.getFireTime(), ZoneId.of("Europe/Stockholm")));

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        archiver.archiveByggrAttachments(start, end, BatchTrigger.SCHEDULED);
    }
}