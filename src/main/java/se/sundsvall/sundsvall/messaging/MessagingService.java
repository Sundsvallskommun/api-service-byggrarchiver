package se.sundsvall.sundsvall.messaging;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.exceptions.mappers.ServerResponseExceptionMapper;
import se.sundsvall.sundsvall.SundsvallsKommunOauth2Filter;
import se.sundsvall.sundsvall.messaging.vo.EmailRequest;
import se.sundsvall.sundsvall.messaging.vo.MessageStatusResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
@RegisterProvider(SundsvallsKommunOauth2Filter.class)
@RegisterProvider(ServerResponseExceptionMapper.class)
@RegisterRestClient(configKey = "MESSAGING")
@ApplicationScoped
public interface MessagingService {

    /**
     * Send an e-mail (independent from feedback settings)
     *
     * @param emailRequest  (required)
     * @return MessageStatusResponse
     */
    @POST
    @Path("messages/email")
    MessageStatusResponse postEmail(@RequestBody EmailRequest emailRequest) throws ServiceException;
}
