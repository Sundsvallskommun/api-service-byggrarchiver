package se.sundsvall.byggrarchiver.integration.arendeexport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;

import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import generated.se.sundsvall.arendeexport.GetUpdatedArendenResponse;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;

@ExtendWith(MockitoExtension.class)
class ArendeExportIntegrationTest {

	@Mock
	private ArendeExportClient mockClient;

	@InjectMocks
	private ArendeExportIntegration integration;

	@Test
	void getUpdatedArendenError() {
		when(mockClient.getUpdatedArenden(any(GetUpdatedArenden.class)))
			.thenThrow(SOAPFaultException.class);

		final var batchFilter = new BatchFilter();

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> integration.getUpdatedArenden(batchFilter))
			.satisfies(throwableProblem -> {
				assertThat(throwableProblem.getStatus()).isEqualTo(SERVICE_UNAVAILABLE);
				assertThat(throwableProblem.getDetail()).startsWith("ArendeExport integration failed");
			});
	}

	@Test
	void getDocumentError() {
		when(mockClient.getDocument(any(GetDocument.class)))
			.thenThrow(SOAPFaultException.class);

		final var randomUuid = UUID.randomUUID().toString();

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> integration.getDocument(randomUuid))
			.satisfies(throwableProblem -> {
				assertThat(throwableProblem.getStatus()).isEqualTo(SERVICE_UNAVAILABLE);
				assertThat(throwableProblem.getDetail()).startsWith("ArendeExport integration failed");
			});
	}

	@Test
	void getDocument() {
		// Arrange
		final var documentResponse = new GetDocumentResponse().withGetDocumentResult(new Dokument());
		final var documentRequest = new GetDocument()
			.withDocumentId("documentId")
			.withInkluderaFil(true);

		when(mockClient.getDocument(any())).thenReturn(documentResponse);

		// Act
		final var response = integration.getDocument(documentRequest.getDocumentId());

		// Assert and verify
		assertThat(response).isEqualTo(documentResponse.getGetDocumentResult());
		verify(mockClient).getDocument(any());
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void getUpdatedArenden() {
		// Arrange
		final var updatedArendenResponse = new GetUpdatedArendenResponse();
		final var updatedArendenRequest = new GetUpdatedArenden()
			.withFilter(new BatchFilter());

		when(mockClient.getUpdatedArenden(any())).thenReturn(updatedArendenResponse);

		// Act
		final var response = integration.getUpdatedArenden(updatedArendenRequest.getFilter());

		// Assert and verify
		assertThat(response).isEqualTo(updatedArendenResponse.getGetUpdatedArendenResult());
		verify(mockClient).getUpdatedArenden(any());
		verifyNoMoreInteractions(mockClient);
	}

}
