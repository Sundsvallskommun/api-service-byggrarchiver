package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor()
public class BatchHistoryResponse {

	private Long id;

	private LocalDate start;

	private LocalDate end;

	private ArchiveStatus archiveStatus;

	private BatchTrigger batchTrigger;

	private LocalDateTime timestamp;

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final BatchHistoryResponse that = (BatchHistoryResponse) o;
		return Objects.equals(id, that.id) && Objects.equals(start, that.start) && Objects.equals(end, that.end) && archiveStatus == that.archiveStatus && batchTrigger == that.batchTrigger && Objects.equals(timestamp, that.timestamp);
	}

	@Override

	public int hashCode() {

		return Objects.hash(id, start

			, end, archiveStatus, batchTrigger, timestamp);
	}

	@Override
	public String toString() {
		return "BatchHistoryResponse{" +
			"id=" + id +
			", start=" + start +
			", end=" + end +
			", archiveStatus=" + archiveStatus +
			", batchTrigger=" + batchTrigger +
			", timestamp=" + timestamp +
			'}';
	}

}
