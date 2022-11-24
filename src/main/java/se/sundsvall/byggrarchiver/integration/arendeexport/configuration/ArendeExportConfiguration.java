package se.sundsvall.byggrarchiver.integration.arendeexport.configuration;


import feign.jaxb.JAXBContextFactory;
import feign.soap.SOAPDecoder;
import feign.soap.SOAPEncoder;
import feign.soap.SOAPErrorDecoder;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

import javax.xml.soap.SOAPConstants;
import java.nio.charset.StandardCharsets;

@Import(FeignClientsConfiguration.class)
public class ArendeExportConfiguration {
    private static final JAXBContextFactory JAXB_FACTORY = new JAXBContextFactory.Builder()
            .withMarshallerJAXBEncoding(StandardCharsets.UTF_8.toString())
            .build();

    private static final SOAPEncoder.Builder ENCODER_BUILDER = new SOAPEncoder.Builder()
            .withCharsetEncoding(StandardCharsets.UTF_8)
            .withFormattedOutput(false)
            .withJAXBContextFactory(JAXB_FACTORY)
            .withSOAPProtocol(SOAPConstants.SOAP_1_1_PROTOCOL)
            .withWriteXmlDeclaration(true);

    @Bean
    FeignBuilderCustomizer feignBuilderCustomizer(ArendeExportProperties properties) {
        return FeignMultiCustomizer.create()
                .withEncoder(ENCODER_BUILDER.build())
                .withDecoder(new SOAPDecoder(JAXB_FACTORY))
                .withErrorDecoder(new SOAPErrorDecoder())
                .withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
                .composeCustomizersToOne();
    }
}