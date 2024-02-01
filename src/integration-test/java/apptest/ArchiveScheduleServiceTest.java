package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import se.sundsvall.byggrarchiver.Application;
import se.sundsvall.byggrarchiver.service.ArchiverScheduleService;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@WireMockAppTestSuite(
    files = "classpath:/IntegrationTest/",
    classes = Application.class
)
@TestPropertySource(properties = {"byggrarchiver.cron.expression=* * * ? * *"})
class ArchiveScheduleServiceTest extends AbstractAppTest {

    @SpyBean
    private ArchiverScheduleService archiverScheduleService;

    @Test
    void archive() {
        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> verify(archiverScheduleService, atLeast(9)).archive());
    }
}
