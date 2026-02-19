package se.sundsvall.byggrarchiver.integration.archive;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import static se.sundsvall.byggrarchiver.integration.archive.ArchiveIntegration.INTEGRATION_NAME;

@Import(FeignConfiguration.class)
class ArchiveConfiguration {

	private final ArchiveProperties archiveProperties;

	ArchiveConfiguration(final ArchiveProperties archiveProperties) {
		this.archiveProperties = archiveProperties;
	}

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer() {
		return FeignMultiCustomizer.create()
			.withRetryableOAuth2InterceptorForClientRegistration(ClientRegistration.withRegistrationId(INTEGRATION_NAME)
				.tokenUri(archiveProperties.oauth2().tokenUrl())
				.clientId(archiveProperties.oauth2().clientId())
				.clientSecret(archiveProperties.oauth2().clientSecret())
				.authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
				.build())
			.withErrorDecoder(new ProblemErrorDecoder(INTEGRATION_NAME))
			.composeCustomizersToOne();
	}
}
