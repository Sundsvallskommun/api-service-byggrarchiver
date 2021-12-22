package se.sundsvall.exceptions.mappers;

import org.jboss.logging.Logger;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.Information;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(NotFoundException exception) {
        log.info(exception.getLocalizedMessage());

        Information info = new Information(Constants.RFC_LINK_NOT_FOUND, exception.getResponse().getStatusInfo().getReasonPhrase(),
                exception.getResponse().getStatusInfo().getStatusCode(), exception.getLocalizedMessage(), null);

        return Response.status(exception.getResponse().getStatusInfo()).entity(info).build();
    }
}
