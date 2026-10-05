package se.sundsvall.byggrarchiver.integration.archive;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class ArchiveIntegration {

	static final String INTEGRATION_NAME = "archive";

	private static final Logger LOG = LoggerFactory.getLogger(ArchiveIntegration.class);

	private final ArchiveClient archiveClient;

	ArchiveIntegration(final ArchiveClient archiveClient) {
		this.archiveClient = archiveClient;
	}

	public ArchiveResponse archive(final ByggRArchiveRequest archiveRequest, final String municipalityId) {
		final var attachment = archiveRequest.getAttachment();
		LOG.info("Calling Archive for municipalityId: {} with attachment: {} ({} bytes base64-encoded)", municipalityId, attachment.getName(),
			ofNullable(attachment.getFile()).map(String::length).orElse(0));
		final var startTime = System.currentTimeMillis();

		try {
			final var response = archiveClient.postArchive(municipalityId, archiveRequest);
			final var archiveId = ofNullable(response).map(ArchiveResponse::getArchiveId).orElse(null);

			LOG.info("Archive returned archiveId: {} for attachment: {} in {} ms", archiveId, attachment.getName(), System.currentTimeMillis() - startTime);
			return response;
		} catch (final RuntimeException e) {
			LOG.warn("Archive call for attachment: {} failed after {} ms", attachment.getName(), System.currentTimeMillis() - startTime);
			throw e;
		}
	}

}
