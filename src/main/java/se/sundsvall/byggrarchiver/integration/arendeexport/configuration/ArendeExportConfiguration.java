package se.sundsvall.byggrarchiver.integration.arendeexport.configuration;


import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jaxb.JAXBContextFactory;
import feign.soap.SOAPDecoder;
import feign.soap.SOAPEncoder;
import feign.soap.SOAPErrorDecoder;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignHelper;

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
    Encoder feignSOAPEncoder() {
        return ENCODER_BUILDER.build();
    }

    @Bean
    Decoder feignSOAPDecoder() {
        return new SOAPDecoder(JAXB_FACTORY);
    }

    @Bean
    ErrorDecoder errorDecoder() {
        return new SOAPErrorDecoder();
    }

    @Bean
    FeignBuilderCustomizer feignBuilderCustomizer(ArendeExportProperties properties) {
        return FeignHelper.customizeRequestOptions()
                .withConnectTimeout(properties.getConnectTimeout())
                .withReadTimeout(properties.getReadTimeout())
                .build();
    }
}