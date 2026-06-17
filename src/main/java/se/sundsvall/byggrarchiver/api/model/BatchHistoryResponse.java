package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;

@Data
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
}
