package se.sundsvall.byggrarchiver.integration.messaging;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration.INTEGRATION_NAME;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageResult;

@FeignClient(
	name = INTEGRATION_NAME,
	url = "${integration.messaging.url}",
	configuration = MessagingIntegrationConfiguration.class)
public interface MessagingClient {

	@PostMapping(
		path = "/{municipalityId}/email",
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	MessageResult sendEmail(
		@PathVariable("municipalityId") String municipalityId,
		@RequestBody EmailRequest emailRequest);

}
