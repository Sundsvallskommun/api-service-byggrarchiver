package se.sundsvall.byggrarchiver.configuration;

import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
	LongTermArchiveProperties.class, EmailProperties.class
})
class PropertiesConfiguration {

	@Bean
	Clock clock() {
		return Clock.systemDefaultZone();
	}

}
