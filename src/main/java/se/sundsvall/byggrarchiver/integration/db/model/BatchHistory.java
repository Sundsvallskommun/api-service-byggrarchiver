package se.sundsvall.byggrarchiver.integration.db.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder(setterPrefix = "with")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "batch_history", indexes =
	{
		@jakarta.persistence.Index(name = "batch_history_municipality_id_idx", columnList = "municipalityId"),
		@jakarta.persistence.Index(name = "batch_history_archive_status_idx", columnList = "archiveStatus")
	})
public class BatchHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@Column(nullable = false)
	private Long id;

	@Column
	private String municipalityId;

	@Column(nullable = false)
	private LocalDate start;

	@Column(nullable = false)
	private LocalDate end;

	@Column(nullable = false, columnDefinition = "varchar(255)")
	@Enumerated(EnumType.STRING)
	private ArchiveStatus archiveStatus;

	@Column(nullable = false, columnDefinition = "varchar(255)")
	@Enumerated(EnumType.STRING)
	private BatchTrigger batchTrigger;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@Column(nullable = false)
	private LocalDateTime timestamp;

	@PrePersist
	@PreUpdate
	protected void onPersist() {
		timestamp = LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MICROS);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final BatchHistory that = (BatchHistory) o;
		return Objects.equals(id, that.id) && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(start, that.start) && Objects.equals(end, that.end) && archiveStatus == that.archiveStatus && batchTrigger == that.batchTrigger && Objects.equals(timestamp, that.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, municipalityId, start, end, archiveStatus, batchTrigger, timestamp);
	}

	@Override
	public String toString() {
		return "BatchHistory{" +
			"id=" + id +
			", municipalityId='" + municipalityId + '\'' +
			", start=" + start +
			", end=" + end +
			", archiveStatus=" + archiveStatus +
			", batchTrigger=" + batchTrigger +
			", timestamp=" + timestamp +
			'}';
	}

}
