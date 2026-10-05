package se.sundsvall.byggrarchiver.integration;

import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;

/**
 * Buffers the error response body before delegating. dept44's error decoders and Feign's SOAPErrorDecoder read the body
 * twice, which only works on a buffered body. Feign logging used to buffer it as a side effect, but is turned off for
 * the archive and arendeExport clients (see application.yml). Without the body, the error message lacks the reason,
 * and for archive {@code ArchiveFormatRejectionPredicate} can no longer detect a format rejection.
 */
public class BufferingErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder delegate;

	public BufferingErrorDecoder(final ErrorDecoder delegate) {
		this.delegate = delegate;
	}

	@Override
	public Exception decode(final String methodKey, final Response response) {
		if (response.body() == null) {
			return delegate.decode(methodKey, response);
		}

		try (var inputStream = response.body().asInputStream()) {
			return delegate.decode(methodKey, response.toBuilder().body(inputStream.readAllBytes()).build());
		} catch (final IOException _) {
			return delegate.decode(methodKey, response);
		}
	}

}
