package se.sundsvall.byggrarchiver.integration.arendeexport;

import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Service
public class ArendeExportIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(ArendeExportIntegration.class);

	private final ArendeExportClient arendeExportClient;

	public ArendeExportIntegration(final ArendeExportClient arendeExportClient) {
		this.arendeExportClient = arendeExportClient;
	}

	public ArendeBatch getUpdatedArenden(final BatchFilter filter) {
		try {
			final var request = new GetUpdatedArenden();
			request.setFilter(filter);
			return arendeExportClient.getUpdatedArenden(request).getGetUpdatedArendenResult();
		} catch (final SOAPFaultException e) {
			LOG.warn("ArendeExport integration failed ('GetUpdatedArenden')", e);

			throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, "ArendeExport integration failed ('GetUpdatedArenden')");
		}
	}

	public List<Dokument> getDocument(final String dokId) {
		try {
			final var getDocument = new GetDocument();
			getDocument.setDocumentId(dokId);
			return arendeExportClient.getDocument(getDocument).getGetDocumentResult();
		} catch (final SOAPFaultException e) {
			LOG.warn("ArendeExport integration failed ('GetDocument')", e);

			throw Problem.valueOf(Status.SERVICE_UNAVAILABLE, "ArendeExport integration failed ('GetDocument')");
		}
	}

}
