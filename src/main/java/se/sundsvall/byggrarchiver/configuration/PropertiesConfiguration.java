package se.sundsvall.byggrarchiver.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LongTermArchiveProperties.class, EmailProperties.class})
class PropertiesConfiguration {

}
