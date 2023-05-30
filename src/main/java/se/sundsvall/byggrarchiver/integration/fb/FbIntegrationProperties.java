package se.sundsvall.byggrarchiver.integration.fb;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.fb")
record FbIntegrationProperties(

    @NotBlank
    String url,

    @NotBlank
    String username,

    @NotBlank
    String password,

    @NotBlank
    String database) { }
