package se.sundsvall.byggrarchiver.integration.arendeexport;

import static se.sundsvall.byggrarchiver.integration.arendeexport.ArendeExportConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import generated.se.sundsvall.arendeexport.GetDocument;
import generated.se.sundsvall.arendeexport.GetDocumentResponse;
import generated.se.sundsvall.arendeexport.GetUpdatedArenden;
import generated.se.sundsvall.arendeexport.GetUpdatedArendenResponse;

@FeignClient(name = CLIENT_ID, url = "${integration.arendeexport.url}", configuration = ArendeExportConfiguration.class)
public interface ArendeExportClient {

	String TEXT_XML_UTF8 = "text/xml;charset=UTF-8";

	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=www.tekis.se/ServiceContract/V4/IExportArenden/GetUpdatedArenden"})
	GetUpdatedArendenResponse getUpdatedArenden(GetUpdatedArenden request);

	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=www.tekis.se/ServiceContract/V4/IExportArenden/GetDocument"})
	GetDocumentResponse getDocument(GetDocument request);

}
