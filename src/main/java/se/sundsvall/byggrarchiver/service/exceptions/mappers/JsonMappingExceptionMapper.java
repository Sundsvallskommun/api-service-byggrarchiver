package se.sundsvall.byggrarchiver.service.exceptions.mappers;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.jboss.logging.Logger;
import se.sundsvall.byggrarchiver.service.util.Constants;
import se.sundsvall.byggrarchiver.api.model.Information;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(JsonMappingException exception) {
        log.info(exception.getLocalizedMessage());

        Information info = new Information(Constants.RFC_LINK_BAD_REQUEST, Status.BAD_REQUEST.getReasonPhrase(),
                Status.BAD_REQUEST.getStatusCode(), exception.getOriginalMessage(), exception.getLocation() != null ? exception.getLocation().toString() : null);
        return Response.status(Status.BAD_REQUEST).entity(info).build();

    }

}
