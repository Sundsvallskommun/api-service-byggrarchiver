package se.sundsvall.byggrarchiver.integration.messaging;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration.INTEGRATION_NAME;

@FeignClient(
	name = INTEGRATION_NAME,
	url = "${integration.messaging.url}",
	configuration = MessagingIntegrationConfiguration.class)
@CircuitBreaker(name = INTEGRATION_NAME)
public interface MessagingClient {

	@PostMapping(
		path = "/{municipalityId}/email",
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	MessageResult sendEmail(
		@PathVariable("municipalityId") String municipalityId,
		@RequestBody EmailRequest emailRequest);

}
