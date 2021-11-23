package se.sundsvall.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ArchiveBatchHistory {
    public ArchiveBatchHistory(LocalDate start, LocalDate end, BatchStatus batchStatus) {
        this.start = start;
        this.end = end;
        this.batchStatus = batchStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    private Long id;
    private LocalDate start;
    private LocalDate end;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchStatus batchStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchiveBatchHistory that = (ArchiveBatchHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(start, that.start) && Objects.equals(end, that.end) && batchStatus == that.batchStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, batchStatus);
    }
}
