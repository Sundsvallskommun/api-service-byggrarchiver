package se.sundsvall.byggrarchiver.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;

@Entity
@Builder(setterPrefix = "with")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "archive_failure", indexes = {
	@Index(name = "archive_failure_batch_history_id_idx", columnList = "batchHistoryId"),
	@Index(name = "archive_failure_municipality_id_idx", columnList = "municipalityId"),
	@Index(name = "archive_failure_failure_category_idx", columnList = "failureCategory")
})
public class ArchiveFailure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private Long batchHistoryId;

	// Nullable on purpose: a failure whose case has no dnr is still worth auditing - never let a null caseId
	// drop the audit row via a constraint violation.
	@Column
	private String caseId;

	@Column
	private String documentId;

	@Column
	private String municipalityId;

	@Column
	private String documentName;

	@Column(nullable = false, columnDefinition = "varchar(255)")
	@Enumerated(EnumType.STRING)
	private FailureCategory failureCategory;

	@Column(columnDefinition = "varchar(255)")
	private String message;

	@Column(columnDefinition = "longtext")
	private String detail;

	@Column(nullable = false)
	private LocalDateTime timestamp;

	@PrePersist
	@PreUpdate
	protected void onPersist() {
		timestamp = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MICROS);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final ArchiveFailure that = (ArchiveFailure) o;
		return Objects.equals(id, that.id) && Objects.equals(batchHistoryId, that.batchHistoryId) && Objects.equals(caseId, that.caseId) && Objects.equals(documentId, that.documentId) && Objects.equals(municipalityId, that.municipalityId) && Objects
			.equals(documentName, that.documentName) && failureCategory == that.failureCategory && Objects.equals(message, that.message) && Objects.equals(detail, that.detail) && Objects.equals(timestamp, that.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, batchHistoryId, caseId, documentId, municipalityId, documentName, failureCategory, message, detail, timestamp);
	}

	@Override
	public String toString() {
		return "ArchiveFailure{" +
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
