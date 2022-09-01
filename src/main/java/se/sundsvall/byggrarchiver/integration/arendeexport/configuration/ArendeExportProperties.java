package se.sundsvall.byggrarchiver.integration.arendeexport.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties("integration.arendeexport")
@Getter
@Setter
public class ArendeExportProperties {

	private Duration connectTimeout;
	private Duration readTimeout;
}
