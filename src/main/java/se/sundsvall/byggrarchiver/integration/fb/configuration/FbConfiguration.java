package se.sundsvall.byggrarchiver.integration.fb.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Import(FeignConfiguration.class)
public class FbConfiguration {
    public static final String CLIENT_ID = "fb";

    @Bean
    Encoder encoder() {
        return new JacksonEncoder(new ObjectMapper()
                .setDefaultPropertyInclusion(ALWAYS)); // Feign must be able to send null values.
    }

    @Bean
    ErrorDecoder errorDecoder() {
        return new ProblemErrorDecoder(CLIENT_ID);
    }
}