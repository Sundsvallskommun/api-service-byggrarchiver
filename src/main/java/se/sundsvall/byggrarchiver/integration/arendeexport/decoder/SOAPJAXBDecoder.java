package se.sundsvall.byggrarchiver.integration.arendeexport.decoder;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import se.sundsvall.byggrarchiver.integration.arendeexport.DocumentTooLargeException;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

public class SOAPJAXBDecoder implements Decoder {

	private static final String FETCH_INFORMATION_ERROR = "Couldn't fetch information from ByggR";

	private final long maxDocumentResponseSize;

	/**
	 * @param maximumFileSize the largest file (in bytes) that is archived. GetDocument responses are limited to that size
	 *                        base64-encoded, plus 10% for the SOAP envelope and metadata, so a document that would be
	 *                        rejected as too large anyway is never read into memory.
	 */
	public SOAPJAXBDecoder(final long maximumFileSize) {
		final var base64Size = (maximumFileSize + 2) / 3 * 4;
		this.maxDocumentResponseSize = base64Size + base64Size / 10;
	}

	@Override
	public Object decode(Response response, Type type) throws IOException, FeignException {

		try (InputStream inputStream = response.body().asInputStream()) {
			var rawType = resolveRawType(type);
			var jaxbContext = getJAXBContext(rawType);
			var unmarshaller = jaxbContext.createUnmarshaller();
			var envelope = (SOAPEnvelope) unmarshaller.unmarshal(GetDocumentResponse.class.equals(rawType) ? limitDocumentResponse(response, inputStream) : inputStream);

			if (envelope.getBody() != null) {
				Object bodyContent = envelope.getBody().getContent();
				if (bodyContent instanceof JAXBElement) {
					return ((JAXBElement<?>) bodyContent).getValue();
				}
				return bodyContent;
			}

			// If the body is null, we throw a problem
			throw Problem.builder()
				.withDetail("SOAP response body is empty")
				.withStatus(BAD_GATEWAY)
				.withTitle(FETCH_INFORMATION_ERROR)
				.build();
		} catch (JAXBException e) {
			throw Problem.builder()
				.withStatus(BAD_GATEWAY)
				.withTitle(FETCH_INFORMATION_ERROR)
				.withDetail(e.getMessage())
				.build();
		}

	}

	private InputStream limitDocumentResponse(final Response response, final InputStream inputStream) {
		final var contentLength = response.body().length();
		if ((contentLength != null) && (contentLength > maxDocumentResponseSize)) {
			throw documentTooLarge(response, "Content-Length: " + contentLength);
		}

		// Content-Length is missing for chunked responses, so also stop reading once the limit is passed
		return new FilterInputStream(inputStream) {
			private long bytesRead;

			@Override
			public int read() throws IOException {
				final var result = super.read();
				if (result != -1) {
					count(1);
				}
				return result;
			}

			@Override
			public int read(final byte[] buffer, final int offset, final int length) throws IOException {
				final var result = super.read(buffer, offset, length);
				if (result > 0) {
					count(result);
				}
				return result;
			}

			private void count(final long bytes) {
				bytesRead += bytes;
				if (bytesRead > maxDocumentResponseSize) {
					throw documentTooLarge(response, "more than " + maxDocumentResponseSize + " bytes read");
				}
			}
		};
	}

	private DocumentTooLargeException documentTooLarge(final Response response, final String detail) {
		return new DocumentTooLargeException(response.status(),
			"ByggR GetDocument response is larger than the limit of %d bytes (%s)".formatted(maxDocumentResponseSize, detail),
			response.request());
	}

	private JAXBContext getJAXBContext(Type type) throws JAXBException {
		return JAXBContext.newInstance(SOAPEnvelope.class, (Class<?>) type);
	}

	Type resolveRawType(Type type) {
		while (type instanceof ParameterizedType ptype) {
			type = ptype.getRawType();
		}

		if (!(type instanceof Class)) {
			throw Problem.builder()
				.withStatus(BAD_GATEWAY)
				.withTitle(FETCH_INFORMATION_ERROR)
				.withDetail(String.format("SOAP only supports decoding raw types. Found %s", type))
				.build();
		}

		return type;
	}
}
