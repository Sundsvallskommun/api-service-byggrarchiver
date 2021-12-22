package se.sundsvall.exceptions.mappers;

import org.jboss.logging.Logger;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.Information;

import javax.inject.Inject;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(WebApplicationException exception) {
        log.info(exception.getLocalizedMessage(), exception);

        String type = null;

        if (exception instanceof NotAllowedException) {
            type = Constants.RFC_LINK_NOT_ALLOWED;
        } else if (exception instanceof NotFoundException) {
            type = Constants.RFC_LINK_NOT_FOUND;
        }

        Information info = new Information(type, exception.getResponse().getStatusInfo().getReasonPhrase(),
                exception.getResponse().getStatusInfo().getStatusCode(), exception.getLocalizedMessage(), null);

        return Response.status(exception.getResponse().getStatusInfo()).entity(info).build();
    }
}
