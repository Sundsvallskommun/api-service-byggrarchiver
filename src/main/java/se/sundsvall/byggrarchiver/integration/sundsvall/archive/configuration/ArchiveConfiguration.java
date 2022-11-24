package se.sundsvall.byggrarchiver.integration.sundsvall.archive.configuration;


import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
public class ArchiveConfiguration {
    public static final String REGISTRATION_ID = "archive";

    private final ArchiveProperties archiveProperties;

    public ArchiveConfiguration(ArchiveProperties archiveProperties) {
        this.archiveProperties = archiveProperties;
    }

    @Bean
    FeignBuilderCustomizer feignBuilderCustomizer() {
        return FeignMultiCustomizer.create()
                .withErrorDecoder(new ProblemErrorDecoder(REGISTRATION_ID))
                .withRetryableOAuth2InterceptorForClientRegistration(ClientRegistration.withRegistrationId(REGISTRATION_ID)
                        .tokenUri(archiveProperties.getOauth2TokenUrl())
                        .clientId(archiveProperties.getOauth2ClientId())
                        .clientSecret(archiveProperties.getOauth2ClientSecret())
                        .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                        .build())
                .composeCustomizersToOne();
    }
}