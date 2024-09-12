package se.sundsvall.byggrarchiver.service.exceptions;

import java.io.Serial;

public class ApplicationException extends Exception {

	@Serial
	private static final long serialVersionUID = 8653248280977885930L;

	public ApplicationException(final String message) {
		super(message);
	}

	public ApplicationException(final String message, final Exception e) {
		super(message, e);
	}

}
