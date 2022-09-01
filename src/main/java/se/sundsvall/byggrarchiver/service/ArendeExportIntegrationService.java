package se.sundsvall.byggrarchiver.service;

import arendeexport.ArendeBatch;
import arendeexport.BatchFilter;
import arendeexport.Dokument;
import arendeexport.GetDocument;
import arendeexport.GetUpdatedArenden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportClient;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;

import static se.sundsvall.byggrarchiver.service.util.Constants.ARENDEEXPORT_ERROR_MESSAGE;

@Service
public class ArendeExportIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(ArendeExportIntegrationService.class);
    private static final ThrowableProblem PROBLEM = org.zalando.problem.Problem.valueOf(Status.SERVICE_UNAVAILABLE, ARENDEEXPORT_ERROR_MESSAGE);

    private ArendeExportClient arendeExportClient;

    public ArendeExportIntegrationService(ArendeExportClient arendeExportClient) {
        this.arendeExportClient = arendeExportClient;
    }

    public ArendeBatch getUpdatedArenden(BatchFilter filter) {
        try {
            GetUpdatedArenden getUpdatedArenden = new GetUpdatedArenden();
            getUpdatedArenden.setFilter(filter);
            return arendeExportClient.getUpdatedArenden(getUpdatedArenden).getGetUpdatedArendenResult();
        } catch (SOAPFaultException e) {
            log.info(ARENDEEXPORT_ERROR_MESSAGE, e);
            throw PROBLEM;
        }
    }

    public List<Dokument> getDocument(String dokId) {
        try {
            GetDocument getDocument = new GetDocument();
            getDocument.setDocumentId(dokId);
            return arendeExportClient.getDocument(getDocument).getGetDocumentResult();
        } catch (SOAPFaultException e) {
            log.info(ARENDEEXPORT_ERROR_MESSAGE, e);
            throw PROBLEM;
        }
    }


}
