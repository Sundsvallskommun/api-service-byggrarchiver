package se.sundsvall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import se.sundsvall.casemanagement.SystemType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@NoArgsConstructor
public class ArchiveHistory {

    @Getter
    @Setter
    @Id
    @Column(unique = true)
    private String documentId;

    @Getter
    @Setter
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemType system;

    @Getter
    private LocalDateTime archivedAt;

    @Getter
    @Schema(readOnly = true)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onPersist() {
        timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        archivedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    @PreUpdate
    protected void onUpdate() {
        timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}
