package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;

@Data
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
}
