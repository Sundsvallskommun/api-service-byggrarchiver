package se.sundsvall.byggrarchiver.integration.fb;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
class FbIntegrationConfiguration {

	static final String INTEGRATION_NAME = "fb";

	private final FbIntegrationProperties properties;

	FbIntegrationConfiguration(final FbIntegrationProperties properties) {
		this.properties = properties;
	}

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer() {
		return FeignMultiCustomizer.create()
			.withRequestInterceptor(requestTemplate -> requestTemplate
				.query("Database", properties.database())
				.query("User", properties.username())
				.query("Password", properties.password()))
			.withErrorDecoder(new ProblemErrorDecoder(INTEGRATION_NAME))
			.composeCustomizersToOne();
	}

}
