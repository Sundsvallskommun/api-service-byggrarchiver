package se.sundsvall.byggrarchiver.service.exceptions.mappers;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.jboss.logging.Logger;
import se.sundsvall.byggrarchiver.service.util.Constants;
import se.sundsvall.byggrarchiver.api.model.Information;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(UnrecognizedPropertyException exception) {
        log.info(exception.getLocalizedMessage());

        Information info = new Information(Constants.RFC_LINK_BAD_REQUEST, Status.BAD_REQUEST.getReasonPhrase(),
                Status.BAD_REQUEST.getStatusCode(), "Unrecognized field \"" + exception.getPropertyName()
                + "\". This field must not be used in this object.",
                exception.getPathReference());
        return Response.status(Status.BAD_REQUEST).entity(info).build();

    }

}
