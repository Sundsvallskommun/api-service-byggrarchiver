package se.sundsvall.byggrarchiver.integration.sundsvall.archive.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "integration.archive")
public class ArchiveProperties {
    private String oauth2TokenUrl;
    private String oauth2ClientId;
    private String oauth2ClientSecret;
}
