package se.sundsvall.byggrarchiver.service;

import arendeexport.ArendeBatch;
import arendeexport.BatchFilter;
import arendeexport.Dokument;
import arendeexport.GetDocumentResponse;
import arendeexport.GetUpdatedArendenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportClient;

import javax.xml.ws.soap.SOAPFaultException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static se.sundsvall.byggrarchiver.service.util.Constants.ARENDEEXPORT_ERROR_MESSAGE;

@ExtendWith(MockitoExtension.class)
class ArendeExportIntegrationServiceTest {

    @Mock
    private ArendeExportClient arendeExportClientMock;

    @InjectMocks
    private ArendeExportIntegrationService arendeExportIntegrationService;

    @Test
    void getUpdatedArenden() {
        BatchFilter batchFilter = new BatchFilter();
        batchFilter.setLowerExclusiveBound(LocalDateTime.now().minusDays(3));
        batchFilter.setUpperInclusiveBound(LocalDateTime.now());

        GetUpdatedArendenResponse getUpdatedArendenResponse = new GetUpdatedArendenResponse();
        ArendeBatch arendeBatch = new ArendeBatch();
        arendeBatch.setBatchStart(batchFilter.getLowerExclusiveBound());
        arendeBatch.setBatchEnd(batchFilter.getUpperInclusiveBound());
        getUpdatedArendenResponse.setGetUpdatedArendenResult(arendeBatch);

        doReturn(getUpdatedArendenResponse).when(arendeExportClientMock).getUpdatedArenden(any());

        var result = arendeExportIntegrationService.getUpdatedArenden(batchFilter);
        assertEquals(arendeBatch, result);
    }

    @Test
    void getUpdatedArendenError() {
        doThrow(SOAPFaultException.class).when(arendeExportClientMock).getUpdatedArenden(any());

        var batchFilter = new BatchFilter();
        ThrowableProblem throwableProblem = assertThrows(ThrowableProblem.class, () -> arendeExportIntegrationService.getUpdatedArenden(batchFilter));
        assertEquals(Status.SERVICE_UNAVAILABLE, throwableProblem.getStatus());
        assertEquals(Status.SERVICE_UNAVAILABLE.getReasonPhrase(), throwableProblem.getTitle());
        assertEquals(ARENDEEXPORT_ERROR_MESSAGE, throwableProblem.getDetail());
    }

    @Test
    void getDocument() {
        String dokId = UUID.randomUUID().toString();
        GetDocumentResponse getDocumentResponse = new GetDocumentResponse();
        Dokument dokument = new Dokument();
        dokument.setNamn("Test");
        dokument.setDokId(dokId);
        getDocumentResponse.getGetDocumentResult().add(dokument);
        doReturn(getDocumentResponse).when(arendeExportClientMock).getDocument(any());
        var result = arendeExportIntegrationService.getDocument(dokId);

        assertEquals(getDocumentResponse.getGetDocumentResult(), result);
    }

    @Test
    void getDocumentError() {
        doThrow(SOAPFaultException.class).when(arendeExportClientMock).getDocument(any());

        var randomUuid = UUID.randomUUID().toString();
        ThrowableProblem throwableProblem = assertThrows(ThrowableProblem.class, () -> arendeExportIntegrationService.getDocument(randomUuid));
        assertEquals(Status.SERVICE_UNAVAILABLE, throwableProblem.getStatus());
        assertEquals(Status.SERVICE_UNAVAILABLE.getReasonPhrase(), throwableProblem.getTitle());
        assertEquals(ARENDEEXPORT_ERROR_MESSAGE, throwableProblem.getDetail());
    }

}
