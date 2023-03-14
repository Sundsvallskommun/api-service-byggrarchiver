package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDateTime;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArchiveHistoryResponse {

    private String documentId;
    private String caseId;
    private String documentName;
    private String documentType;
    private String archiveId;
    private String archiveUrl;
    private ArchiveStatus archiveStatus;
    private LocalDateTime timestamp;
    private BatchHistoryResponse batchHistory;
}
