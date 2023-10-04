package apptest;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import se.sundsvall.byggrarchiver.Application;
import se.sundsvall.byggrarchiver.service.ArchiverScheduleService;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@TestPropertySource(properties = { "cron.expression=* * * ? * *" })
@WireMockAppTestSuite(
	files = "classpath:/IntegrationTest/",
	classes = Application.class)
class ArchiveScheduleServiceTest extends AbstractAppTest {

	@SpyBean
	private ArchiverScheduleService archiverScheduleService;

	@Test
	void archive() {
		await()
			.atMost(Duration.ofSeconds(30))
			.untilAsserted(() -> verify(archiverScheduleService, atLeast(5)).archive());
	}
}
