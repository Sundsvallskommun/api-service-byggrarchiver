package se.sundsvall.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import se.sundsvall.sundsvall.casemanagement.SystemType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@IdClass(IdPk.class)
@NoArgsConstructor
public class ArchiveHistory {


    @Getter
    @Setter
    @Id
    private String documentId;

    @Getter
    @Setter
    @Id
    @Enumerated(EnumType.STRING)
    private SystemType systemType;

    @Getter
    @Setter
    private String archiveId;

    @Getter
    @Setter
    private String archiveUrl;

    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @Setter
    @NotNull
    @ManyToOne
    private BatchHistory batchHistory;

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
    public String toString() {
        return "ArchiveHistory{" +
                "documentId='" + documentId + '\'' +
                ", systemType=" + systemType +
                ", batchHistory=" + batchHistory +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
class IdPk implements Serializable {

    private String documentId;
    private SystemType systemType;
}
