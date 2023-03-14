package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BatchHistoryResponse {

    private Long id;
    private LocalDate start;
    private LocalDate end;
    private ArchiveStatus archiveStatus;
    private BatchTrigger batchTrigger;
    private LocalDateTime timestamp;
}
