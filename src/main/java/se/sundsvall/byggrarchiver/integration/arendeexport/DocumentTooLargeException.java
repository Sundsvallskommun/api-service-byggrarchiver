package se.sundsvall.byggrarchiver.integration.arendeexport;

import feign.FeignException;
import feign.Request;
import java.io.Serial;

/**
 * Thrown when a ByggR GetDocument response is larger than the archive accepts. The response is rejected before (or
 * while) it is read, so an oversized document never gets loaded into memory.
 * <p>
 * Extends {@link FeignException} so Feign rethrows it as is instead of wrapping it in a DecodeException.
 */
public class DocumentTooLargeException extends FeignException {

	@Serial
	private static final long serialVersionUID = 2894757301722536416L;

	public DocumentTooLargeException(final int status, final String message, final Request request) {
		super(status, message, request);
	}

}
