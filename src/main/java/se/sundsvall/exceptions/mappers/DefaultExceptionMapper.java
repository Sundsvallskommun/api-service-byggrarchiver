package se.sundsvall.exceptions.mappers;

import org.jboss.logging.Logger;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.Information;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(Throwable exception) {
        log.error(exception.getLocalizedMessage(), exception);

        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new Information(Constants.RFC_LINK_INTERNAL_SERVER_ERROR, Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.ERR_MSG_UNHANDLED_EXCEPTION, null))
                .build();
    }
}
