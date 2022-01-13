package se.sundsvall.integration.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Random;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOG = LoggerFactory.getLogger(WireMockLifecycleManager.class);

    private static final int PORT_RANGE_MIN = 1024;
    private static final int PORT_RANGE_MAX = 65535;

    private static final Random RANDOM = new Random(System.nanoTime());

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        Options options = options()
            .port(findAvailablePort())
            .usingFilesUnderClasspath("");
        wireMockServer = new WireMockServer(options);
        wireMockServer.start();

        LOG.info("WireMock server listening on port {}", wireMockServer.port());

        // Save the WireMock port for later, to be able to use it for URL:s in the "it" profile
        // parts of application.properties/yml
        System.setProperty("wiremock.server.port", Integer.toString(wireMockServer.port()));

        return Map.of();
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;

            System.clearProperty("wiremock.server.port");
        }
    }

    @Override
    public void inject(final TestInjector testInjector) {
        testInjector.injectIntoFields(wireMockServer,
            new TestInjector.AnnotatedAndMatchesType(InjectWireMock.class, WireMockServer.class));
    }

    private int findAvailablePort() {
        int portRange = PORT_RANGE_MAX - PORT_RANGE_MIN;
        int candidatePort;
        int searchCounter = 0;

        do {
            if (searchCounter > portRange) {
                throw new IllegalStateException(String.format(
                    "Could not find an available port in the range [%d, %d] after %d attempts",
                        PORT_RANGE_MIN, PORT_RANGE_MAX, searchCounter));
            }
            candidatePort = findRandomPort();
            searchCounter++;
        } while (!isPortAvailable(candidatePort));

        return candidatePort;
    }

    private int findRandomPort() {
        int portRange = PORT_RANGE_MAX - PORT_RANGE_MIN;

        return PORT_RANGE_MIN + RANDOM.nextInt(portRange + 1);
    }

    private boolean isPortAvailable(final int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(
                port, 1, InetAddress.getByName("localhost"));
            serverSocket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
