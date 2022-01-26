package se.sundsvall.unit.support;

import org.mockito.ArgumentMatcher;
import se.sundsvall.sundsvall.archive.ArchiveMessage;
import se.sundsvall.vo.Attachment;

/**
 * Only matches based on attachment-field
 */
public class ArchiveMessageAttachmentMatcher implements ArgumentMatcher<ArchiveMessage> {
    private final ArchiveMessage left;

    public ArchiveMessageAttachmentMatcher(ArchiveMessage left) {
        this.left = left;
    }

    @Override
    public boolean matches(ArchiveMessage right) {
        Attachment aLeft = left.getAttachment();
        Attachment aRight = right.getAttachment();

        if (aLeft.equals(aRight)) return true;
        return aLeft.getCategory().equals(aRight.getCategory())
                && aLeft.getName().equals(aRight.getName())
                && aLeft.getNote().equals(aRight.getNote())
                && aLeft.getExtension().equals(aRight.getExtension())
                && aLeft.getMimeType().equals(aRight.getMimeType())
                && aLeft.getFile().equals(aRight.getFile());
    }
}

