package se.sundsvall.byggrarchiver.integration.db.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class BatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate start;

    @Column(nullable = false)
    private LocalDate end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArchiveStatus archiveStatus;

    @Column(nullable = false)
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BatchHistory that)) {
            return false;
        }
        return Objects.equals(start, that.start) && Objects.equals(end, that.end) && archiveStatus == that.archiveStatus && batchTrigger == that.batchTrigger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, archiveStatus, batchTrigger);
    }
}
