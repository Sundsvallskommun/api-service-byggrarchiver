package se.sundsvall.byggrarchiver.service.scheduler;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("scheduler")
public record SchedulerProperties(List<String> municipalityIds) {

}
