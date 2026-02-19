package se.sundsvall.byggrarchiver.service.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.byggrarchiver.Application;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class SchedulerPropertiesTest {

	@Autowired
	private SchedulerProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.municipalityIds()).containsExactly("2281", "2282", "2283");
	}

}
