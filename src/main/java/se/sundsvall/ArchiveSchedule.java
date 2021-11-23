package se.sundsvall;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
public class ArchiveSchedule {

    @Inject
    Logger log;

    @Inject
    Archiver archiver;


    @Scheduled(cron = "{cron.expression}")
    void archive(ScheduledExecution execution) {
        log.info("Archiving... Timestamp: " + LocalDateTime.ofInstant(execution.getFireTime(), ZoneId.of("Europe/Stockholm")));

        archiver.archiveByggrAttachments();
    }
}