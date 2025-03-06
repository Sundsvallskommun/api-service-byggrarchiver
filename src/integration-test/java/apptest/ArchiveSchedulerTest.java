package apptest;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import se.sundsvall.byggrarchiver.Application;
import se.sundsvall.byggrarchiver.service.scheduler.ArchiverScheduler;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(
	files = "classpath:/IntegrationTest/",
	classes = Application.class
)
@TestPropertySource(properties = { "scheduler.cron.expression=* * * ? * *" })
class ArchiveSchedulerTest extends AbstractAppTest {

	@MockitoSpyBean
	private ArchiverScheduler archiverScheduler;

	@Test
	void archive() {
		await()
			.atMost(Duration.ofSeconds(10))
			.untilAsserted(() -> verify(archiverScheduler, atLeast(9)).archive());
	}

}
