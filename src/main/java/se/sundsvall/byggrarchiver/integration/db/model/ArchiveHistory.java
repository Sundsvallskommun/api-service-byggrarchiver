package se.sundsvall.byggrarchiver.integration.db.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(IdPk.class)
@Builder(setterPrefix = "with")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "archive_history", indexes =
	{
		@Index(name = "archive_history_municipality_id_idx", columnList = "municipalityId"),
		@Index(name = "archive_history_archive_status_idx", columnList = "archiveStatus")
	})
public class ArchiveHistory {

	@Id
	private String documentId;

	@Id
	private String caseId;

	private String municipalityId;

	private String documentName;

	private String documentType;

	private String archiveId;

	private String archiveUrl;

	@Column(nullable = false, columnDefinition = "varchar(255)")
	@Enumerated(EnumType.STRING)
	private ArchiveStatus archiveStatus;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@Column(nullable = false)
	private LocalDateTime timestamp;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "batch_history_id", foreignKey = @ForeignKey(name = "fk_archive_history_batch_history_id"))
	private BatchHistory batchHistory;

	@PrePersist
	@PreUpdate
	protected void onPersist() {
		timestamp = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MICROS);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final ArchiveHistory that = (ArchiveHistory) o;
		return Objects.equals(documentId, that.documentId) && Objects.equals(caseId, that.caseId) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(documentName, that.documentName) && Objects.equals(documentType, that.documentType) && Objects.equals(archiveId, that.archiveId) && Objects.equals(archiveUrl, that.archiveUrl) && archiveStatus == that.archiveStatus && Objects.equals(timestamp, that.timestamp) && Objects.equals(batchHistory, that.batchHistory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(documentId, caseId, municipalityId, documentName, documentType, archiveId, archiveUrl, archiveStatus, timestamp, batchHistory);
	}

	@Override
	public String toString() {
		return "ArchiveHistory{" +
			"documentId='" + documentId + '\'' +
			", caseId='" + caseId + '\'' +
			", municipalityId='" + municipalityId + '\'' +
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

@Embeddable
class IdPk implements Serializable {

	private String documentId;

	private String caseId;

}
