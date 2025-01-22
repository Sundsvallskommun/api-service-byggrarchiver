package se.sundsvall.byggrarchiver.service.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.service.ByggrArchiverService;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;

@Service
public class ArchiverScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(ArchiverScheduler.class);

	private final ByggrArchiverService byggrArchiverService;
	private final SchedulerProperties schedulerProperties;

	public ArchiverScheduler(final ByggrArchiverService byggrArchiverService, final SchedulerProperties schedulerProperties) {
		this.byggrArchiverService = byggrArchiverService;
		this.schedulerProperties = schedulerProperties;

		if ("-".equals(schedulerProperties.cron().expression())) {
			LOG.info("ArchiverScheduler is DISABLED");
		} else {
			LOG.info("ArchiverScheduler is ENABLED, with cron expression {}", schedulerProperties.cron().expression());
		}
	}

	@Dept44Scheduled(cron = "${scheduler.cron.expression}",
		name = "${scheduler.name}",
		lockAtMostFor = "${scheduler.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.maximum-execution-time}")
	public void archive() {
		final var now = LocalDateTime.now(ZoneId.systemDefault());

		LOG.info("Running archiving on schedule. Timestamp: {}", now);

		// Run batch from one week back in time to yesterday
		final var oneWeekBack = now.toLocalDate().minusDays(7);
		final var yesterday = now.toLocalDate().minusDays(1);

		schedulerProperties.municipalityIds().forEach(municipalityId -> byggrArchiverService.runBatch(oneWeekBack, yesterday, BatchTrigger.SCHEDULED, municipalityId));
	}
}
