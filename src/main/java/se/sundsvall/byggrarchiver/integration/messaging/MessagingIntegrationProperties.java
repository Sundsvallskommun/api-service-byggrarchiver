package se.sundsvall.byggrarchiver.integration.messaging;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import se.sundsvall.byggrarchiver.integration.OAuth2;

@Validated
@ConfigurationProperties(prefix = "integration.messaging")
record MessagingIntegrationProperties(

	@NotBlank String url,

	@NotNull @Valid OAuth2 oauth2) {

}
