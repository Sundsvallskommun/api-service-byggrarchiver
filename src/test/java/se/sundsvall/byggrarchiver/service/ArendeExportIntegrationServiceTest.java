package se.sundsvall.byggrarchiver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.xml.ws.soap.SOAPFaultException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportIntegration;

import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import generated.se.sundsvall.arendeexport.GetUpdatedArendenResponse;

@ExtendWith(MockitoExtension.class)
class ArendeExportIntegrationServiceTest {

    @Mock
    private ArendeExportIntegration mockArendeExportClient;

    @InjectMocks
    private ArendeExportIntegrationService arendeExportIntegrationService;

    @Test
    void getUpdatedArenden() {
        var batchFilter = new BatchFilter()
            .withLowerExclusiveBound(LocalDateTime.now().minusDays(3))
            .withUpperInclusiveBound(LocalDateTime.now());
        var arendeBatch = new ArendeBatch()
            .withBatchStart(batchFilter.getLowerExclusiveBound())
            .withBatchEnd(batchFilter.getUpperInclusiveBound());
        var response = new GetUpdatedArendenResponse()
            .withGetUpdatedArendenResult(arendeBatch);

        when(mockArendeExportClient.getUpdatedArenden(any(GetUpdatedArenden.class)))
            .thenReturn(response);

        var result = arendeExportIntegrationService.getUpdatedArenden(batchFilter);

        assertThat(result).isEqualTo(arendeBatch);

        verify(mockArendeExportClient, times(1)).getUpdatedArenden(any(GetUpdatedArenden.class));
        verifyNoMoreInteractions(mockArendeExportClient);
    }

    @Test
    void getUpdatedArendenError() {
        when(mockArendeExportClient.getUpdatedArenden(any(GetUpdatedArenden.class)))
            .thenThrow(SOAPFaultException.class);

        var batchFilter = new BatchFilter();

        assertThatExceptionOfType(ThrowableProblem.class)
            .isThrownBy(() -> arendeExportIntegrationService.getUpdatedArenden(batchFilter))
            .satisfies(throwableProblem -> {
                assertThat(throwableProblem.getStatus()).isEqualTo(SERVICE_UNAVAILABLE);
                assertThat(throwableProblem.getDetail()).startsWith("ArendeExport integration failed");
            });
    }

    @Test
    void getDocument() {
        var dokId = UUID.randomUUID().toString();
        var dokument = new Dokument();
        dokument.setDokId(dokId);
        dokument.setNamn("Test");
        var response = new GetDocumentResponse();
        response.getGetDocumentResult().add(dokument);

        doReturn(response).when(mockArendeExportClient).getDocument(any());

        var result = arendeExportIntegrationService.getDocument(dokId);

        assertThat(result).isEqualTo(response.getGetDocumentResult());
    }

    @Test
    void getDocumentError() {
        when(mockArendeExportClient.getDocument(any(GetDocument.class)))
            .thenThrow(SOAPFaultException.class);

        var randomUuid = UUID.randomUUID().toString();

        assertThatExceptionOfType(ThrowableProblem.class)
            .isThrownBy(() -> arendeExportIntegrationService.getDocument(randomUuid))
            .satisfies(throwableProblem -> {
                assertThat(throwableProblem.getStatus()).isEqualTo(SERVICE_UNAVAILABLE);
                assertThat(throwableProblem.getDetail()).startsWith("ArendeExport integration failed");
            });
    }
}
