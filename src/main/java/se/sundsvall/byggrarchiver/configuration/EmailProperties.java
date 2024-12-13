package se.sundsvall.byggrarchiver.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("email")
public record EmailProperties(

	@Valid @NotNull Instance lantmateriet,

	@Valid @NotNull Instance extensionError,

	@Valid @NotNull Instance status) {

	public record Instance(@NotBlank String sender, String replyTo, @NotBlank String recipient) {}
}
