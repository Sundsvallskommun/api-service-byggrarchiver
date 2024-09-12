package se.sundsvall.byggrarchiver.integration;

import jakarta.validation.constraints.NotBlank;

public record OAuth2(

	@NotBlank
	String tokenUrl,

	@NotBlank
	String clientId,

	@NotBlank
	String clientSecret) {

}
