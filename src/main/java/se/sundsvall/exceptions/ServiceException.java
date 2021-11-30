package se.sundsvall.exceptions;

import lombok.Getter;

import javax.ws.rs.core.Response.Status;


public class ServiceException extends Exception {

    @Getter
    private final Status status;

    private ServiceException(final String message, final Throwable cause, final Status status) {
        super(message, cause);

        this.status = status;
    }

    public static ServiceException create(final String message, final Throwable cause, final Status status) {
        return new ServiceException(message, cause, status);
    }

}
