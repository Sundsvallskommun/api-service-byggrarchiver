package se.sundsvall.byggrarchiver.service.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Service
public class ArchiverScheduler {

	private static final Logger log = LoggerFactory.getLogger(ArchiverScheduler.class);

	private final ByggrArchiverService byggrArchiverService;

	private final SchedulerProperties schedulerProperties;

	public ArchiverScheduler(final ByggrArchiverService byggrArchiverService, final SchedulerProperties schedulerProperties) {
		this.byggrArchiverService = byggrArchiverService;
		this.schedulerProperties = schedulerProperties;
	}

	@Scheduled(cron = "${scheduler.cron.expression}")
	@SchedulerLock(name = "archive", lockAtMostFor = "${scheduler.shedlock-lock-at-most-for}")
	public void archive() {
		log.info("Running archiving on schedule. Timestamp: {}", LocalDateTime.now(ZoneId.systemDefault()));

		// Run batch from one week back in time to yesterday
		final LocalDate oneWeekBack = LocalDate.now(ZoneId.systemDefault()).minusDays(7);
		final LocalDate yesterday = LocalDate.now(ZoneId.systemDefault()).minusDays(1);

		schedulerProperties.municipalityIds().forEach(municipalityId
			-> byggrArchiverService.runBatch(oneWeekBack, yesterday, BatchTrigger.SCHEDULED, municipalityId));
	}

}
