package se.sundsvall.byggrarchiver.service.scheduler;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("scheduler")
public record SchedulerProperties(@NotNull @Valid Cron cron, @NotEmpty List<@NotBlank String> municipalityIds) {

	public record Cron(@NotBlank String expression) {}
}
