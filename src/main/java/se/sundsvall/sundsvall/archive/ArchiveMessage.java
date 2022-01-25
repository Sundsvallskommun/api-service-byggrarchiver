package se.sundsvall.sundsvall.archive;

import lombok.Getter;
import lombok.Setter;
import se.sundsvall.vo.Attachment;

@Getter
@Setter
public class ArchiveMessage {
    private Attachment attachment;
    private String metadata;
}
