package se.sundsvall.byggrarchiver.integration.arendeexport;

import org.springframework.stereotype.Component;

import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import generated.se.sundsvall.arendeexport.GetUpdatedArendenResponse;

@Component
public class ArendeExportIntegration {

	private final ArendeExportClient arendeExportClient;

	public ArendeExportIntegration(final ArendeExportClient arendeExportClient) {
		this.arendeExportClient = arendeExportClient;
	}

	public GetUpdatedArendenResponse getUpdatedArenden(final GetUpdatedArenden request) {
		return arendeExportClient.getUpdatedArenden(request);
	}

	public GetDocumentResponse getDocument(final GetDocument request) {
		return arendeExportClient.getDocument(request);
	}

}
