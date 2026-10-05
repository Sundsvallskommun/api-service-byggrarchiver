package se.sundsvall.byggrarchiver.integration.archive;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArchiveIntegrationTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private ArchiveClient mockClient;

	@InjectMocks
	private ArchiveIntegration integration;

	@Test
	void archive() {
		final var request = new ByggRArchiveRequest().attachment(new Attachment().name("file.pdf").file("ZmlsZQ=="));
		final var archiveResponse = new ArchiveResponse().archiveId("archiveId");
		when(mockClient.postArchive(MUNICIPALITY_ID, request)).thenReturn(archiveResponse);

		final var result = integration.archive(request, MUNICIPALITY_ID);

		assertThat(result).isSameAs(archiveResponse);
		verify(mockClient).postArchive(MUNICIPALITY_ID, request);
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void archiveErrorIsRethrown() {
		final var request = new ByggRArchiveRequest().attachment(new Attachment().name("file.pdf"));
		final var exception = new IllegalStateException("read timeout");
		when(mockClient.postArchive(MUNICIPALITY_ID, request)).thenThrow(exception);

		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> integration.archive(request, MUNICIPALITY_ID))
			.isSameAs(exception);
	}

}
