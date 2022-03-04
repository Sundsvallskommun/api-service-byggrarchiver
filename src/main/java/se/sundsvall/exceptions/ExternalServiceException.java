package se.sundsvall.exceptions;

import se.sundsvall.util.Constants;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ExternalServiceException extends WebApplicationException {

    public ExternalServiceException() {
        super(Constants.ERR_MSG_EXTERNAL_SERVICE, Response.Status.SERVICE_UNAVAILABLE);
    }
}
