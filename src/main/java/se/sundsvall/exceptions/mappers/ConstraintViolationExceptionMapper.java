package se.sundsvall.exceptions.mappers;

import org.jboss.logging.Logger;
import se.sundsvall.Constants;
import se.sundsvall.vo.Information;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        log.info(exception.getLocalizedMessage());

        List<Information> infoList = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            Information info = new Information(Constants.RFC_LINK_BAD_REQUEST, Status.BAD_REQUEST.getReasonPhrase(),
                    Status.BAD_REQUEST.getStatusCode(), violation.getMessage(), violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : null);
            infoList.add(info);

        }
        return Response.status(Status.BAD_REQUEST).entity(infoList).build();
    }

}
