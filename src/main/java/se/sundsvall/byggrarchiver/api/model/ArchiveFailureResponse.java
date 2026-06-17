package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;

@Setter
@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor()
public class ArchiveFailureResponse {

	private Long id;

	private Long batchHistoryId;

	private String caseId;

	private String documentId;

	private String municipalityId;

	private String documentName;

	private FailureCategory failureCategory;

	private String message;

	private String detail;

	private LocalDateTime timestamp;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final ArchiveFailureResponse that = (ArchiveFailureResponse) o;
		return Objects.equals(id, that.id) && Objects.equals(batchHistoryId, that.batchHistoryId) && Objects.equals(caseId, that.caseId) && Objects.equals(documentId, that.documentId) && Objects.equals(municipalityId, that.municipalityId) && Objects
			.equals(documentName, that.documentName) && failureCategory == that.failureCategory && Objects.equals(message, that.message) && Objects.equals(detail, that.detail) && Objects.equals(timestamp, that.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, batchHistoryId, caseId, documentId, municipalityId, documentName, failureCategory, message, detail, timestamp);
	}

	@Override
	public String toString() {
		return "ArchiveFailureResponse{" +
			"id=" + id +
			", batchHistoryId=" + batchHistoryId +
			", caseId='" + caseId + '\'' +
			", documentId='" + documentId + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", documentName='" + documentName + '\'' +
			", failureCategory=" + failureCategory +
			", message='" + message + '\'' +
			", detail='" + detail + '\'' +
			", timestamp=" + timestamp +
			'}';
	}

}
