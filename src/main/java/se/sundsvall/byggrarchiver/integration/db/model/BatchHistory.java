package se.sundsvall.byggrarchiver.integration.db.model;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import se.sundsvall.byggrarchiver.api.model.BatchTrigger;
import se.sundsvall.byggrarchiver.api.model.Status;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BatchHistory {
    public BatchHistory(LocalDate start, LocalDate end, BatchTrigger batchTrigger, Status status) {
        this.start = start;
        this.end = end;
        this.batchTrigger = batchTrigger;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    @Column(nullable = false)
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate start;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private BatchTrigger batchTrigger;

    @Getter
    @Schema(readOnly = true)
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchHistory that = (BatchHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(start, that.start) && Objects.equals(end, that.end) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, status);
    }
}
