package se.sundsvall.byggrarchiver.configuration;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("long-term-archive")
public record LongTermArchiveProperties(

	@NotBlank String url) {

}
