package se.sundsvall.byggrarchiver.integration.sundsvall.archive.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignHelper;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Import(FeignConfiguration.class)
public class ArchiveConfiguration {
    public static final String INTEGRATION_ID = "archive";

    @Bean
    RequestInterceptor oAuth2RequestInterceptor(ClientRegistrationRepository clientRegistrationRepository) {
        return FeignHelper.oAuth2RequestInterceptor(clientRegistrationRepository.findByRegistrationId(INTEGRATION_ID));
    }

    @Bean
    Encoder encoder() {
        return new JacksonEncoder(new ObjectMapper()
                .setDefaultPropertyInclusion(ALWAYS)); // Feign must be able to send null values.
    }

    @Bean
    ErrorDecoder errorDecoder() {
        return new ProblemErrorDecoder(INTEGRATION_ID);
    }
}