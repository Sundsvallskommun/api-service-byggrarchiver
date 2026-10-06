package se.sundsvall.byggrarchiver.integration;

import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.soap.SOAPErrorDecoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;
import se.sundsvall.dept44.exception.ServerProblem;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BufferingErrorDecoderTest {

	private static final String PROBLEM_BODY = """
		{"detail": "File format validation failed.", "status": 500, "title": "Internal Server Error"}""";

	private static final String HTML_BODY = "<html><body>Service Unavailable</body></html>";

	@Test
	void decodeKeepsBodyOfUnbufferedProblemResponse() {
		final var result = new BufferingErrorDecoder(new ProblemErrorDecoder("archive")).decode("methodKey", unbufferedResponse("application/problem+json", PROBLEM_BODY));

		assertThat(result).isInstanceOf(ServerProblem.class);
		assertThat(result.getMessage()).contains("File format validation failed.");
	}

	@Test
	void decodeKeepsBodyOfUnbufferedNonSoapResponse() {
		// SOAPErrorDecoder first tries to parse a SOAP fault, then falls back to Feign's default decoder
		final var result = new BufferingErrorDecoder(new SOAPErrorDecoder()).decode("methodKey", unbufferedResponse("text/html", HTML_BODY));

		assertThat(result).isInstanceOf(FeignException.class);
		assertThat(result.getMessage()).contains("Service Unavailable");
	}

	@Test
	void decodeWithoutBody() {
		final var response = Response.builder()
			.status(500)
			.request(request())
			.build();

		final var result = new BufferingErrorDecoder(new ProblemErrorDecoder("archive")).decode("methodKey", response);

		assertThat(result).isInstanceOf(ServerProblem.class);
	}

	@Test
	void decodeDelegatesOriginalResponseWhenBodyCannotBeRead() throws IOException {
		final var delegate = mock(ErrorDecoder.class);
		final var body = mock(Response.Body.class);
		final var response = mock(Response.class);
		final var exception = new IllegalStateException();
		when(response.body()).thenReturn(body);
		when(body.asInputStream()).thenThrow(new IOException("closed"));
		when(delegate.decode("methodKey", response)).thenReturn(exception);

		final var result = new BufferingErrorDecoder(delegate).decode("methodKey", response);

		assertThat(result).isSameAs(exception);
		verify(delegate).decode("methodKey", response);
	}

	// Like an OkHttp response body when Feign logging is off: the stream can only be read once
	private static Response unbufferedResponse(final String contentType, final String content) {
		final var body = content.getBytes(UTF_8);
		return Response.builder()
			.status(500)
			.headers(Map.of("Content-Type", List.of(contentType)))
			.request(request())
			.body(new ByteArrayInputStream(body), body.length)
			.build();
	}

	private static Request request() {
		return Request.create(Request.HttpMethod.POST, "http://localhost", Map.of(), null, UTF_8, null);
	}

}
