package se.sundsvall;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.sundsvall.casemanagement.SystemType;
import se.sundsvall.vo.ArchiveHistory;
import se.sundsvall.vo.BatchTrigger;

import javax.inject.Inject;
import java.time.LocalDate;

@QuarkusTest
class ArchiveIntegrationTest {

    @Inject
    Archiver archiver;

    @Inject
    ArchiveDao archiveDao;

    static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8090).usingFilesUnderDirectory("src/integration-test/resources"));

    @BeforeAll
    public static void beforeAll() {
        wireMockServer.start();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    void testScheduledJob() throws ApplicationException {
        archiver.archiveByggrAttachments(LocalDate.now().minusDays(1), LocalDate.now(), BatchTrigger.SCHEDULED);

        ArchiveHistory archiveHistory = archiveDao.getArchiveHistory("586154", SystemType.BYGGR);
        Assertions.assertEquals(1, archiveHistory.getBatchHistory().getId());

    }
}