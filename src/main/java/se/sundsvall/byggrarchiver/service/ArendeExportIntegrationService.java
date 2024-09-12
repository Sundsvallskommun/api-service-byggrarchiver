package se.sundsvall.byggrarchiver.service;

import java.util.List;

import jakarta.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportIntegration;

import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;

@Service
public class ArendeExportIntegrationService {

	private static final Logger LOG = LoggerFactory.getLogger(ArendeExportIntegrationService.class);

	private final ArendeExportIntegration arendeExportIntegration;

	public ArendeExportIntegrationService(final ArendeExportIntegration arendeExportIntegration) {
		this.arendeExportIntegration = arendeExportIntegration;
	}

	public ArendeBatch getUpdatedArenden(BatchFilter filter) {
		try {
			final var request = new GetUpdatedArenden();
			request.setFilter(filter);
			return arendeExportIntegration.getUpdatedArenden(request).getGetUpdatedArendenResult();
		} catch (final SOAPFaultException e) {
			LOG.warn("ArendeExport integration failed ('GetUpdatedArenden')", e);

			throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, "ArendeExport integration failed ('GetUpdatedArenden')");
		}
	}

	public List<Dokument> getDocument(String dokId) {
		try {
			final var getDocument = new GetDocument();
			getDocument.setDocumentId(dokId);
			return arendeExportIntegration.getDocument(getDocument).getGetDocumentResult();
		} catch (final SOAPFaultException e) {
			LOG.warn("ArendeExport integration failed ('GetDocument')", e);

			throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, "ArendeExport integration failed ('GetDocument')");
		}
	}

}
