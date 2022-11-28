package se.sundsvall.byggrarchiver.integration.sundsvall.messaging;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageStatusResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.byggrarchiver.integration.sundsvall.messaging.configuration.MessagingConfiguration;

@FeignClient(name = "messaging", url = "${integration.messaging.url}", configuration = MessagingConfiguration.class)
@CircuitBreaker(name = "messaging")
public interface MessagingClient {

    /**
     * Send an e-mail (independent of feedback settings)
     *
     * @param emailRequest (required)
     * @return MessageStatusResponse
     */
    @PostMapping(path = "/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    MessageStatusResponse postEmail(@RequestBody EmailRequest emailRequest) throws ServiceException;

}
