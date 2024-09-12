package se.sundsvall.byggrarchiver.integration.arendeexport;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("integration.arendeexport")
record ArendeExportProperties(int connectTimeout, int readTimeout) {

}
