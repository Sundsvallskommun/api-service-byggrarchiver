package se.sundsvall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BatchHistory {
    public BatchHistory(LocalDate start, LocalDate end, BatchTrigger batchTrigger, BatchStatus batchStatus) {
        this.start = start;
        this.end = end;
        this.batchTrigger = batchTrigger;
        this.batchStatus = batchStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate start;

    @Column(nullable = false)
    private LocalDate end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchStatus batchStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
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
        return Objects.equals(id, that.id) && Objects.equals(start, that.start) && Objects.equals(end, that.end) && batchStatus == that.batchStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, batchStatus);
    }
}
