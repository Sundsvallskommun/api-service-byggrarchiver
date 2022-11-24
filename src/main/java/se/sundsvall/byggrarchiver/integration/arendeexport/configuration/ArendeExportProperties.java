package se.sundsvall.byggrarchiver.integration.arendeexport.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.arendeexport")
public record ArendeExportProperties(int connectTimeout, int readTimeout) {
}