package se.sundsvall.byggrarchiver.testutils;

import org.mockito.ArgumentMatcher;

import generated.se.sundsvall.archive.ByggRArchiveRequest;

public class ArchiveMessageAttachmentMatcher implements ArgumentMatcher<ByggRArchiveRequest> {

	private final ByggRArchiveRequest left;

	public ArchiveMessageAttachmentMatcher(final ByggRArchiveRequest left) {
		this.left = left;
	}

	@Override
	public boolean matches(final ByggRArchiveRequest right) {
		final var aLeft = left.getAttachment();
		final var aRight = right.getAttachment();

		if (aLeft.equals(aRight)) {
			return true;
		}

		return aLeft.getName().equals(aRight.getName())
			&& aLeft.getExtension().equals(aRight.getExtension())
			&& aLeft.getFile().equals(aRight.getFile());
	}

}
