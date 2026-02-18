package se.sundsvall.byggrarchiver.integration.arendeexport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.byggrarchiver.Application;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class ArendeExportPropertiesTest {

	@Autowired
	private ArendeExportProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(1);
		assertThat(properties.readTimeout()).isEqualTo(2);
	}

}
