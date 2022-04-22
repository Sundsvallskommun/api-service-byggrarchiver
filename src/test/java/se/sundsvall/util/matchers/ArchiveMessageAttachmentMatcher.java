package se.sundsvall.util.matchers;

import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import org.mockito.ArgumentMatcher;

/**
 * Only matches based on attachment-field
 */
public class ArchiveMessageAttachmentMatcher implements ArgumentMatcher<ByggRArchiveRequest> {
    private final ByggRArchiveRequest left;

    public ArchiveMessageAttachmentMatcher(ByggRArchiveRequest left) {
        this.left = left;
    }

    @Override
    public boolean matches(ByggRArchiveRequest right) {
        Attachment aLeft = left.getAttachment();
        Attachment aRight = right.getAttachment();

        if (aLeft.equals(aRight)) return true;
        return aLeft.getName().equals(aRight.getName())
                && aLeft.getExtension().equals(aRight.getExtension())
                && aLeft.getFile().equals(aRight.getFile());
    }
}

