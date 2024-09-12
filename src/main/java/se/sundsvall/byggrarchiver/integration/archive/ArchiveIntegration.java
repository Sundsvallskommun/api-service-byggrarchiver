package se.sundsvall.byggrarchiver.integration.archive;

import org.springframework.stereotype.Component;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.ByggRArchiveRequest;

@Component
public class ArchiveIntegration {

	static final String INTEGRATION_NAME = "archive";

	private final ArchiveClient archiveClient;

	public ArchiveIntegration(final ArchiveClient archiveClient) {
		this.archiveClient = archiveClient;
	}

	public ArchiveResponse archive(final ByggRArchiveRequest archiveRequest, final String municipalityId) {
		return archiveClient.postArchive(municipalityId, archiveRequest);
	}

}
