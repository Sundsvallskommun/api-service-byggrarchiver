package se.sundsvall.byggrarchiver.integration.arendeexport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import generated.se.sundsvall.arendeexport.GetUpdatedArendenResponse;

@ExtendWith(MockitoExtension.class)
class ArendeExportIntegrationTest {

	@Mock
	private ArendeExportClient mockClient;

	@InjectMocks
	private ArendeExportIntegration integration;

	@Test
	void getDocument() {
		// Arrange
		final var documentResponse = new GetDocumentResponse();
		final var documentRequest = new GetDocument()
			.withInkluderaFil(true);

		when(mockClient.getDocument(any())).thenReturn(documentResponse);

		// Act
		final var response = integration.getDocument(documentRequest);

		// Assert and verify
		assertThat(response).isEqualTo(documentResponse);
		verify(mockClient).getDocument(documentRequest);
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
		final var response = integration.getUpdatedArenden(updatedArendenRequest);

		// Assert and verify
		assertThat(response).isEqualTo(updatedArendenResponse);
		verify(mockClient).getUpdatedArenden(updatedArendenRequest);
		verifyNoMoreInteractions(mockClient);
	}
}
