package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;

@Setter
@Getter
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

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final ArchiveHistoryResponse that = (ArchiveHistoryResponse) o;
		return Objects.equals(documentId, that.documentId) && Objects.equals(caseId, that.caseId) && Objects.equals(documentName, that.documentName) && Objects.equals(documentType, that.documentType) && Objects.equals(archiveId, that.archiveId) && Objects
			.equals(archiveUrl, that.archiveUrl) && archiveStatus == that.archiveStatus && Objects.equals(timestamp, that.timestamp) && Objects.equals(batchHistory, that.batchHistory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(documentId, caseId, documentName, documentType, archiveId, archiveUrl, archiveStatus, timestamp, batchHistory);
	}

	@Override
	public String toString() {
		return "ArchiveHistoryResponse{" +
			"documentId='" + documentId + '\'' +
			", caseId='" + caseId + '\'' +
			", documentName='" + documentName + '\'' +
			", documentType='" + documentType + '\'' +
			", archiveId='" + archiveId + '\'' +
			", archiveUrl='" + archiveUrl + '\'' +
			", archiveStatus=" + archiveStatus +
			", timestamp=" + timestamp +
			", batchHistory=" + batchHistory +
			'}';
	}

}
