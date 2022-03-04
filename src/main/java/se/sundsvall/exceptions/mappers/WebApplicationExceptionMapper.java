package se.sundsvall.exceptions.mappers;

import org.jboss.logging.Logger;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.Information;

import javax.inject.Inject;
import javax.ws.rs.*;
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

        String type;

        switch (exception.getResponse().getStatus()) {
            case 400:
                type = Constants.RFC_LINK_BAD_REQUEST;
                break;
            case 404:
                type = Constants.RFC_LINK_NOT_FOUND;
                break;
            case 405:
                type = Constants.RFC_LINK_NOT_ALLOWED;
                break;
            case 500:
                type = Constants.RFC_LINK_INTERNAL_SERVER_ERROR;
                break;
            case 501:
                type = Constants.RFC_LINK_NOT_IMPLEMENTED;
                break;
            case 503:
                type = Constants.RFC_LINK_SERVICE_UNAVAILABLE;
                break;
            default:
                type = null;
                break;
        }

        Information info = new Information(type, exception.getResponse().getStatusInfo().getReasonPhrase(),
                exception.getResponse().getStatusInfo().getStatusCode(), exception.getLocalizedMessage(), null);

        return Response.status(exception.getResponse().getStatusInfo()).entity(info).build();
    }
}
