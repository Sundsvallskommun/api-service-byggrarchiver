package se.sundsvall.byggrarchiver.integration.sundsvall.messaging.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "integration.messaging")
public class MessagingProperties {
    private String oauth2TokenUrl;
    private String oauth2ClientId;
    private String oauth2ClientSecret;
}
