package se.sundsvall.byggrarchiver.integration.messaging;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
import static se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration.INTEGRATION_NAME;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
class MessagingIntegrationConfiguration {

	private final MessagingIntegrationProperties properties;

	MessagingIntegrationConfiguration(final MessagingIntegrationProperties properties) {
		this.properties = properties;
	}

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer() {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(INTEGRATION_NAME))
			.withRetryableOAuth2InterceptorForClientRegistration(ClientRegistration.withRegistrationId(INTEGRATION_NAME)
				.tokenUri(properties.oauth2().tokenUrl())
				.clientId(properties.oauth2().clientId())
				.clientSecret(properties.oauth2().clientSecret())
				.authorizationGrantType(new AuthorizationGrantType(CLIENT_CREDENTIALS.getValue()))
				.build())
			.composeCustomizersToOne();
	}

}
