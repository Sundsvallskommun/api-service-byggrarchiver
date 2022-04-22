package se.sundsvall.byggrarchiver.integration.sundsvall.messaging;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageStatusResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.sundsvall.byggrarchiver.integration.sundsvall.SundsvallsKommunOauth2Filter;
import se.sundsvall.byggrarchiver.service.exceptions.ServiceException;
import se.sundsvall.byggrarchiver.service.exceptions.mappers.ServerResponseExceptionMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
@RegisterProvider(SundsvallsKommunOauth2Filter.class)
@RegisterProvider(ServerResponseExceptionMapper.class)
@RegisterRestClient(configKey = "MESSAGING")
@ApplicationScoped
public interface MessagingClient {

    /**
     * Send an e-mail (independent from feedback settings)
     *
     * @param emailRequest (required)
     * @return MessageStatusResponse
     */
    @POST
    @Path("messages/email")
    MessageStatusResponse postEmail(@RequestBody EmailRequest emailRequest) throws ServiceException;

}
