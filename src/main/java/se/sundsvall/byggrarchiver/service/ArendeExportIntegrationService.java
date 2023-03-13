package se.sundsvall.byggrarchiver.service;

import static se.sundsvall.byggrarchiver.service.util.Constants.ARENDEEXPORT_ERROR_MESSAGE;

import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportClient;

import arendeexport.ArendeBatch;
import arendeexport.BatchFilter;
import arendeexport.Dokument;
import arendeexport.GetDocument;
import arendeexport.GetUpdatedArenden;

@Service
public class ArendeExportIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(ArendeExportIntegrationService.class);

    private final ArendeExportClient arendeExportClient;

    public ArendeExportIntegrationService(ArendeExportClient arendeExportClient) {
        this.arendeExportClient = arendeExportClient;
    }

    public ArendeBatch getUpdatedArenden(BatchFilter filter) {
        try {
            GetUpdatedArenden getUpdatedArenden = new GetUpdatedArenden();
            getUpdatedArenden.setFilter(filter);
            return arendeExportClient.getUpdatedArenden(getUpdatedArenden).getGetUpdatedArendenResult();
        } catch (SOAPFaultException e) {
            LOG.info(ARENDEEXPORT_ERROR_MESSAGE, e);
            throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, ARENDEEXPORT_ERROR_MESSAGE);
        }
    }

    public List<Dokument> getDocument(String dokId) {
        try {
            GetDocument getDocument = new GetDocument();
            getDocument.setDocumentId(dokId);
            return arendeExportClient.getDocument(getDocument).getGetDocumentResult();
        } catch (SOAPFaultException e) {
            LOG.info(ARENDEEXPORT_ERROR_MESSAGE, e);
            throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, ARENDEEXPORT_ERROR_MESSAGE);
        }
    }
}
