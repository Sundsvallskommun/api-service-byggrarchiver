package se.sundsvall;

import org.mockito.ArgumentMatcher;
import se.sundsvall.sundsvall.casemanagement.Attachment;

public class AttachmentMatcher implements ArgumentMatcher<Attachment> {

    private final Attachment left;

    public AttachmentMatcher(Attachment left) {
        this.left = left;
    }

    @Override
    public boolean matches(Attachment right) {
        if (left.equals(right)) return true;
        return left.getCategory().equals(right.getCategory())
                && left.getName().equals(right.getName())
                && left.getNote().equals(right.getNote())
                && left.getExtension().equals(right.getExtension())
                && left.getMimeType().equals(right.getMimeType())
                && left.getArchiveMetadata().equals(right.getArchiveMetadata())
                && left.getFile().equals(right.getFile());
    }
}
