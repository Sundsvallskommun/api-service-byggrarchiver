package se.sundsvall.byggrarchiver.integration.arendeexport;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import se.sundsvall.byggrarchiver.integration.arendeexport.configuration.ArendeExportConfiguration;

import arendeexport.GetDocument;
import arendeexport.GetDocumentResponse;
import arendeexport.GetUpdatedArenden;
import arendeexport.GetUpdatedArendenResponse;

@FeignClient(name = "arendeexport", url = "${integration.arendeexport.url}", configuration = ArendeExportConfiguration.class)
public interface ArendeExportClient {

    String TEXT_XML_UTF8 = "text/xml;charset=UTF-8";

    @PostMapping(consumes = TEXT_XML_UTF8, headers = {"SOAPAction=www.tekis.se/ServiceContract/V4/IExportArenden/GetUpdatedArenden"})
    GetUpdatedArendenResponse getUpdatedArenden(GetUpdatedArenden getUpdatedArenden);

    @PostMapping(consumes = TEXT_XML_UTF8, headers = {"SOAPAction=www.tekis.se/ServiceContract/V4/IExportArenden/GetDocument"})
    GetDocumentResponse getDocument(GetDocument getDocument);
}
