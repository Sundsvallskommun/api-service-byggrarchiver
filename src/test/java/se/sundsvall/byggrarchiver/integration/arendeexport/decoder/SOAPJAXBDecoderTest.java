package se.sundsvall.byggrarchiver.integration.arendeexport.decoder;

import feign.Request;
import feign.Response;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetRelateradeArendenByPersOrgNrAndRoleResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Base64;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.integration.arendeexport.DocumentTooLargeException;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ExtendWith({
	MockitoExtension.class, ResourceLoaderExtension.class
})
class SOAPJAXBDecoderTest {

	// Gives a GetDocument response limit of 4400 bytes (4000 bytes base64 + 10%)
	private static final int MAXIMUM_FILE_SIZE = 3000;

	@Mock
	private Response mockResponse;

	@Mock
	private Response.Body mockResponseBody;

	private SOAPJAXBDecoder decoder;

	@BeforeEach
	void setup() {
		decoder = new SOAPJAXBDecoder(MAXIMUM_FILE_SIZE);
		when(mockResponse.body()).thenReturn(mockResponseBody);
	}

	@Test
	void testDecodeSoapMessage(@Load(as = Load.ResourceType.STRING, value = "soap/junit-soap-byggr-get-related-errands-by-legal-id-response.xml") String xml) throws IOException {
		var inputStream = new ByteArrayInputStream(xml.getBytes());
		when(mockResponseBody.asInputStream()).thenReturn(inputStream);

		var result = decoder.decode(mockResponse, GetRelateradeArendenByPersOrgNrAndRoleResponse.class);

		assertThat(result).isInstanceOf(GetRelateradeArendenByPersOrgNrAndRoleResponse.class);
		assertThat(((GetRelateradeArendenByPersOrgNrAndRoleResponse) result).getGetRelateradeArendenByPersOrgNrAndRoleResult().getArende()).hasSize(3);

		verify(mockResponse).body();
		verify(mockResponseBody).asInputStream();
		verifyNoMoreInteractions(mockResponse, mockResponseBody);
	}

	@Test
	void testDecodeMissingBodyInSoapMessage_shouldThrowProblem(@Load(as = Load.ResourceType.STRING, value = "soap/junit-soap-missing-body.xml") String xml) throws IOException {
		var inputStream = new ByteArrayInputStream(xml.getBytes());
		when(mockResponseBody.asInputStream()).thenReturn(inputStream);

		Assertions.assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> decoder.decode(mockResponse, SOAPEnvelope.class))
			.withMessage("Couldn't fetch information from ByggR: SOAP response body is empty")
			.satisfies(problem -> assertThat(problem.getStatus()).isEqualTo(BAD_GATEWAY));

		verify(mockResponse).body();
		verify(mockResponseBody).asInputStream();
		verifyNoMoreInteractions(mockResponse, mockResponseBody);
	}

	@Test
	void testDecodeFaultySoapMessage_shouldThrowProblem() throws IOException {
		var inputStream = new ByteArrayInputStream("faulty".getBytes());
		when(mockResponseBody.asInputStream()).thenReturn(inputStream);

		Assertions.assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> decoder.decode(mockResponse, GetRelateradeArendenByPersOrgNrAndRoleResponse.class))
			.withMessage("Couldn't fetch information from ByggR")
			.satisfies(problem -> assertThat(problem.getStatus()).isEqualTo(BAD_GATEWAY));

		verify(mockResponse).body();
		verify(mockResponseBody).asInputStream();
		verifyNoMoreInteractions(mockResponse, mockResponseBody);
	}

	@Test
	void testdecodeParameterizedType_shouldThrowProblem(@Load(as = Load.ResourceType.STRING, value = "soap/junit-soap-byggr-get-related-errands-by-legal-id-response.xml") String xml) throws IOException {
		var inputStream = new ByteArrayInputStream(xml.getBytes());
		when(mockResponseBody.asInputStream()).thenReturn(inputStream);

		Type nonRawType = new WildcardType() {
			@NotNull
			@Override
			public Type[] getUpperBounds() {
				return new Type[] {
					Object.class
				};
			}

			@NotNull
			@Override
			public Type[] getLowerBounds() {
				return new Type[] {};
			}

			@Override
			public String toString() {
				return "?";
			}
		};

		Assertions.assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> decoder.decode(mockResponse, nonRawType))
			.withMessage("Couldn't fetch information from ByggR: SOAP only supports decoding raw types. Found ?")
			.satisfies(problem -> assertThat(problem.getStatus()).isEqualTo(BAD_GATEWAY));

		verify(mockResponse).body();
		verify(mockResponseBody).asInputStream();
		verifyNoMoreInteractions(mockResponse, mockResponseBody);
	}

	@Test
	void testDecodeGetDocumentWithinLimit() throws IOException {
		final var xml = getDocumentResponse(2000);
		when(mockResponseBody.asInputStream()).thenReturn(new ByteArrayInputStream(xml.getBytes(UTF_8)));
		when(mockResponseBody.length()).thenReturn(xml.length());

		final var result = decoder.decode(mockResponse, GetDocumentResponse.class);

		assertThat(result).isInstanceOf(GetDocumentResponse.class);
		assertThat(((GetDocumentResponse) result).getGetDocumentResult().getFirst().getFil().getFilBuffer()).hasSize(2000);
	}

	@Test
	void testDecodeGetDocumentWithContentLengthOverLimit_shouldThrowWithoutReadingBody() throws IOException {
		final var xml = getDocumentResponse(5000);
		final var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_8));
		when(mockResponseBody.asInputStream()).thenReturn(inputStream);
		when(mockResponseBody.length()).thenReturn(xml.length());
		when(mockResponse.status()).thenReturn(200);
		when(mockResponse.request()).thenReturn(request());

		Assertions.assertThatExceptionOfType(DocumentTooLargeException.class)
			.isThrownBy(() -> decoder.decode(mockResponse, GetDocumentResponse.class))
			.withMessage("ByggR GetDocument response is larger than 4400 bytes, the limit for a maximum file size of 3000 bytes (Content-Length: %d)".formatted(xml.length()));

		assertThat(inputStream.available()).isEqualTo(xml.length());
	}

	@Test
	void testDecodeGetDocumentWithoutContentLengthOverLimit_shouldThrow() throws IOException {
		when(mockResponseBody.asInputStream()).thenReturn(new ByteArrayInputStream(getDocumentResponse(5000).getBytes(UTF_8)));
		when(mockResponseBody.length()).thenReturn(null);
		when(mockResponse.status()).thenReturn(200);
		when(mockResponse.request()).thenReturn(request());

		Assertions.assertThatExceptionOfType(DocumentTooLargeException.class)
			.isThrownBy(() -> decoder.decode(mockResponse, GetDocumentResponse.class))
			.withMessage("ByggR GetDocument response is larger than 4400 bytes, the limit for a maximum file size of 3000 bytes (more than 4400 bytes read)");
	}

	@Test
	void testDecodeOtherResponseOverLimit_isNotLimited(@Load(as = Load.ResourceType.STRING, value = "soap/junit-soap-byggr-get-related-errands-by-legal-id-response.xml") String xml) throws IOException {
		when(mockResponseBody.asInputStream()).thenReturn(new ByteArrayInputStream(xml.getBytes(UTF_8)));

		final var result = new SOAPJAXBDecoder(1).decode(mockResponse, GetRelateradeArendenByPersOrgNrAndRoleResponse.class);

		assertThat(result).isInstanceOf(GetRelateradeArendenByPersOrgNrAndRoleResponse.class);
		verify(mockResponse).body();
		verify(mockResponseBody).asInputStream();
		verifyNoMoreInteractions(mockResponse, mockResponseBody);
	}

	private static String getDocumentResponse(final int fileSize) {
		return """
			<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body>\
			<GetDocumentResponse xmlns="www.tekis.se/ServiceContract"><GetDocumentResult dokId="1">\
			<fil filAndelse="pdf" xmlns="www.tekis.se/arende"><filBuffer>%s</filBuffer></fil>\
			</GetDocumentResult></GetDocumentResponse></s:Body></s:Envelope>""".formatted(Base64.getEncoder().encodeToString(new byte[fileSize]));
	}

	private static Request request() {
		return Request.create(Request.HttpMethod.POST, "http://byggr", Map.of(), null, UTF_8, null);
	}
}
