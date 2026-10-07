package se.sundsvall.byggrarchiver.integration.arendeexport;

import generated.se.sundsvall.arendeexport.Arende;
import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.ArrayOfArende;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.DokumentFil;
import generated.se.sundsvall.arendeexport.GetArende;
import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
public class ArendeExportIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(ArendeExportIntegration.class);

	private final ArendeExportClient arendeExportClient;

	public ArendeExportIntegration(final ArendeExportClient arendeExportClient) {
		this.arendeExportClient = arendeExportClient;
	}

	public ArendeBatch getUpdatedArenden(final BatchFilter filter) {
		LOG.info("Calling ByggR GetUpdatedArenden with lowerExclusiveBound: {} and upperInclusiveBound: {}", filter.getLowerExclusiveBound(), filter.getUpperInclusiveBound());
		final var startTime = System.currentTimeMillis();
		try {
			final var request = new GetUpdatedArenden();
			request.setFilter(filter);
			final var result = arendeExportClient.getUpdatedArenden(request).getGetUpdatedArendenResult();

			LOG.info("ByggR GetUpdatedArenden returned {} cases (batchStart: {}, batchEnd: {}) in {} ms",
				ofNullable(result).map(ArendeBatch::getArenden).map(ArrayOfArende::getArende).map(List::size).orElse(0),
				ofNullable(result).map(ArendeBatch::getBatchStart).orElse(null),
				ofNullable(result).map(ArendeBatch::getBatchEnd).orElse(null),
				System.currentTimeMillis() - startTime);
			return result;
		} catch (final SOAPFaultException e) {
			LOG.warn("ArendeExport integration failed ('GetUpdatedArenden') after {} ms", System.currentTimeMillis() - startTime, e);

			throw Problem.valueOf(SERVICE_UNAVAILABLE, "ArendeExport integration failed ('GetUpdatedArenden')");
		} catch (final RuntimeException e) {
			LOG.warn("ByggR GetUpdatedArenden failed after {} ms", System.currentTimeMillis() - startTime);
			throw e;
		}
	}

	public List<Dokument> getDocument(final String dokId) {
		LOG.info("Calling ByggR GetDocument for Document-ID: {}", dokId);
		final var startTime = System.currentTimeMillis();
		try {
			final var getDocument = new GetDocument();
			getDocument.setDocumentId(dokId);
			final var result = arendeExportClient.getDocument(getDocument).getGetDocumentResult();

			LOG.info("ByggR GetDocument for Document-ID: {} returned {} file(s) of {} bytes in total in {} ms", dokId, result.size(),
				result.stream()
					.mapToLong(dokument -> ofNullable(dokument.getFil()).map(DokumentFil::getFilBuffer).map(buffer -> buffer.length).orElse(0))
					.sum(),
				System.currentTimeMillis() - startTime);
			return result;
		} catch (final SOAPFaultException e) {
			LOG.warn("ArendeExport integration failed ('GetDocument') for Document-ID: {} after {} ms", dokId, System.currentTimeMillis() - startTime, e);

			throw Problem.valueOf(SERVICE_UNAVAILABLE, "ArendeExport integration failed ('GetDocument')");
		} catch (final RuntimeException e) {
			LOG.warn("ByggR GetDocument for Document-ID: {} failed after {} ms", dokId, System.currentTimeMillis() - startTime);
			throw e;
		}
	}

	public Arende getArende(final String dnr) {
		LOG.info("Calling ByggR GetArende for Case-ID: {}", dnr);
		final var startTime = System.currentTimeMillis();
		try {
			final var result = arendeExportClient.getArende(new GetArende().withDnr(dnr)).getGetArendeResult();
			final var status = ofNullable(result).map(Arende::getStatus).orElse(null);

			LOG.info("ByggR GetArende for Case-ID: {} returned status {} in {} ms", dnr, status, System.currentTimeMillis() - startTime);
			return result;
		} catch (final SOAPFaultException e) {
			LOG.warn("ArendeExport integration failed ('GetArende') for Case-ID: {} after {} ms", dnr, System.currentTimeMillis() - startTime, e);

			throw Problem.valueOf(SERVICE_UNAVAILABLE, "ArendeExport integration failed ('GetArende')");
		} catch (final RuntimeException e) {
			LOG.warn("ByggR GetArende for Case-ID: {} failed after {} ms", dnr, System.currentTimeMillis() - startTime);
			throw e;
		}
	}

}
