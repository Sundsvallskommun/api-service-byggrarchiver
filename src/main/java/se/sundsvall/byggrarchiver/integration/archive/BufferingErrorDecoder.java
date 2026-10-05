package se.sundsvall.byggrarchiver.integration.archive;

import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;

/**
 * Buffers the error response body before delegating. dept44's error decoders read the body twice, which only works on
 * a buffered body. Feign logging used to buffer it as a side effect, but is turned off for the archive client (see
 * application.yml). Without the body, the message lacks the reason, and {@link ArchiveFormatRejectionPredicate} can no
 * longer detect a format rejection.
 */
class BufferingErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder delegate;

	BufferingErrorDecoder(final ErrorDecoder delegate) {
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
