package se.sundsvall.exceptions.mappers;

import org.jboss.logging.Logger;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.Information;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(EntityExistsException exception) {
        log.info(exception.getLocalizedMessage());

        Information info = new Information(Constants.RFC_LINK_BAD_REQUEST, Status.BAD_REQUEST.getReasonPhrase(),
                Status.BAD_REQUEST.getStatusCode(), exception.getLocalizedMessage(), null);
        return Response.status(Status.BAD_REQUEST).entity(info).build();
    }

}
