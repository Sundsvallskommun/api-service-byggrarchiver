package se.sundsvall.byggrarchiver.integration.archive;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.Attachment;
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

	public ArchiveIntegration(final ArchiveClient archiveClient) {
		this.archiveClient = archiveClient;
	}

	public ArchiveResponse archive(final ByggRArchiveRequest archiveRequest, final String municipalityId) {
		final var attachmentName = ofNullable(archiveRequest.getAttachment()).map(Attachment::getName).orElse(null);
		LOG.info("Calling Archive for municipalityId: {} with attachment: {} ({} bytes base64-encoded)", municipalityId, attachmentName,
			ofNullable(archiveRequest.getAttachment()).map(Attachment::getFile).map(String::length).orElse(0));
		final var startTime = System.currentTimeMillis();

		final var response = archiveClient.postArchive(municipalityId, archiveRequest);

		LOG.info("Archive returned archiveId: {} for attachment: {} in {} ms", ofNullable(response).map(ArchiveResponse::getArchiveId).orElse(null), attachmentName,
			System.currentTimeMillis() - startTime);
		return response;
	}

}
