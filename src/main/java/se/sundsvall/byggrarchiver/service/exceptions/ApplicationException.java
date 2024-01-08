package se.sundsvall.byggrarchiver.service.exceptions;

public class ApplicationException extends Exception {

	private static final long serialVersionUID = 8653248280977885930L;

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(String message, Exception e) {
		super(message, e);
	}
}
