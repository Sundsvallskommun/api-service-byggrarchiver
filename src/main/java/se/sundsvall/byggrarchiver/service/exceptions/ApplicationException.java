package se.sundsvall.byggrarchiver.service.exceptions;

public class ApplicationException extends Exception {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception e) {
        super(message, e);
    }
}
