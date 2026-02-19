package se.sundsvall.byggrarchiver.integration.arendeexport;

import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import generated.se.sundsvall.arendeexport.GetUpdatedArendenResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import static se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportConfiguration.INTEGRATION_NAME;

@FeignClient(name = INTEGRATION_NAME, url = "${integration.arendeexport.url}", configuration = ArendeExportConfiguration.class)
@CircuitBreaker(name = INTEGRATION_NAME)
public interface ArendeExportClient {

	String TEXT_XML_UTF8 = "text/xml;charset=UTF-8";

	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {
		"SOAPAction=www.tekis.se/ServiceContract/V4/IExportArenden/GetUpdatedArenden"
	})
	GetUpdatedArendenResponse getUpdatedArenden(GetUpdatedArenden request);

	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {
		"SOAPAction=www.tekis.se/ServiceContract/V4/IExportArenden/GetDocument"
	})
	GetDocumentResponse getDocument(GetDocument request);

}
