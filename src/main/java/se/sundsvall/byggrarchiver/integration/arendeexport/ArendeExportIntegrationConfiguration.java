package se.sundsvall.byggrarchiver.integration.arendeexport;

import static java.time.Duration.ofSeconds;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.zalando.logbook.Logbook;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.dept44.configuration.webservicetemplate.WebServiceTemplateBuilder;

import generated.se.sundsvall.arendeexport.GetDocument;

@Configuration
class ArendeExportIntegrationConfiguration {

    private static final String ACTION_PREFIX = "www.tekis.se/ServiceContract/V4/IExportArenden";

    private static final String ACTION_GET_UPDATED_ARENDEN = ACTION_PREFIX + "/GetUpdatedArenden";
    private static final String ACTION_GET_DOCUMENT = ACTION_PREFIX + "/GetDocument";

    @Bean("integration.arendeexport.ws-template")
    WebServiceTemplate arendeExportWebServiceTemplate(final ArendeExportIntegrationProperties properties, final Logbook logbook) {
        return new WebServiceTemplateBuilder()
            .withBaseUrl(properties.url())
            .withLogbook(logbook)
            .withConnectTimeout(ofSeconds(properties.connectTimeout()))
            .withReadTimeout(ofSeconds(properties.readTimeout()))
            .withClientInterceptor(new SoapFaultInterceptor())
            .withPackagesToScan(List.of(GetDocument.class.getPackageName()))
            .build();
    }

    @Bean("integration.arendeexport.ws-callback.get-updated-arenden")
    WebServiceMessageCallback getUpdatedArendenWebServiceMessageCallback() {
        return message -> ((SoapMessage) message).setSoapAction(ACTION_GET_UPDATED_ARENDEN);
    }

    @Bean("integration.arendeexport.ws-callback.get-document")
    WebServiceMessageCallback getDocumentWebServiceMessageCallback() {
        return message -> ((SoapMessage) message).setSoapAction(ACTION_GET_DOCUMENT);
    }

    static class SoapFaultInterceptor extends ClientInterceptorAdapter {

        private static final Logger LOG = LoggerFactory.getLogger(SoapFaultInterceptor.class);

        @Override
        public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
            return handleFault(messageContext);
        }

        @Override
        public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
            getFault(messageContext).ifPresent(soapFault -> {
                var faultStringOrReason = soapFault.getFaultStringOrReason();

                LOG.error("Handling SOAP fault: {}", faultStringOrReason);

                throw Problem.valueOf(Status.SERVICE_UNAVAILABLE,
                    "ArendeExport integration encountered a SOAP fault: " + faultStringOrReason);
            });

            return true;
        }

        Optional<SoapFault> getFault(final MessageContext messageContext) {
            var soapMessage = (SoapMessage) messageContext.getResponse();
            var soapEnvelope = soapMessage.getEnvelope();
            var soapBody = soapEnvelope.getBody();
            var soapFault = soapBody.getFault();

            return Optional.ofNullable(soapFault);
        }
    }
}