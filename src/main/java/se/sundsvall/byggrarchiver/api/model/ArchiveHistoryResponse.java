package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor()
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
