package se.sundsvall.byggrarchiver.integration.arendeexport;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;

import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import generated.se.sundsvall.arendeexport.GetUpdatedArendenResponse;

@Component
@EnableConfigurationProperties(ArendeExportIntegrationProperties.class)
public class ArendeExportIntegration {

    private final WebServiceTemplate webServiceTemplate;
    private final WebServiceMessageCallback getUpdatedArendenCallback;
    private final WebServiceMessageCallback getDocumentCallback;

    public ArendeExportIntegration(
            @Qualifier("arendeexportWsTemplate")
            final WebServiceTemplate webServiceTemplate,
            @Qualifier("arendeexportWsCallbackGetUpdatedArenden")
            final WebServiceMessageCallback getUpdatedArendenCallback,
            @Qualifier("arendeexportWsCallbackGetDocument")
            final WebServiceMessageCallback getDocumentCallback) {
        this.webServiceTemplate = webServiceTemplate;
        this.getUpdatedArendenCallback = getUpdatedArendenCallback;
        this.getDocumentCallback = getDocumentCallback;
    }

    public GetUpdatedArendenResponse getUpdatedArenden(final GetUpdatedArenden request) {
        return (GetUpdatedArendenResponse) webServiceTemplate.marshalSendAndReceive(request, getUpdatedArendenCallback);
    }

    public GetDocumentResponse getDocument(final GetDocument request) {
        return (GetDocumentResponse) webServiceTemplate.marshalSendAndReceive(request, getDocumentCallback);
    }
}
