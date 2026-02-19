package se.sundsvall.byggrarchiver.integration.arendeexport;

import feign.soap.SOAPEncoder;
import feign.soap.SOAPErrorDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import se.sundsvall.byggrarchiver.integration.arendeexport.decoder.SOAPJAXBDecoder;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArendeExportConfigurationTest {

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Mock
	private FeignBuilderCustomizer feignBuilderCustomizerMock;

	@Mock
	private ArendeExportProperties propertiesMock;

	@Test
	void testFeignBuilderCustomizer() {
		final var configuration = new ArendeExportConfiguration();

		when(propertiesMock.connectTimeout()).thenReturn(1);
		when(propertiesMock.readTimeout()).thenReturn(2);
		when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(feignBuilderCustomizerMock);

		try (var feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			final var customizer = configuration.feignBuilderCustomizer(propertiesMock);

			var soapEncoderCaptor = ArgumentCaptor.forClass(SOAPEncoder.class);
			var soapDecoderCaptor = ArgumentCaptor.forClass(SOAPJAXBDecoder.class);
			var soapErrorDecoderCaptor = ArgumentCaptor.forClass(SOAPErrorDecoder.class);

			verify(feignMultiCustomizerSpy).withErrorDecoder(soapErrorDecoderCaptor.capture());
			verify(feignMultiCustomizerSpy).withEncoder(soapEncoderCaptor.capture());
			verify(feignMultiCustomizerSpy).withDecoder(soapDecoderCaptor.capture());
			verify(propertiesMock).connectTimeout();
			verify(propertiesMock).readTimeout();
			verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(1, 2);
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();

			assertThat(soapErrorDecoderCaptor.getValue()).isNotNull();
			assertThat(soapEncoderCaptor.getValue()).isNotNull();
			assertThat(soapDecoderCaptor.getValue()).isNotNull();
			assertThat(customizer).isSameAs(feignBuilderCustomizerMock);
		}
	}
}
