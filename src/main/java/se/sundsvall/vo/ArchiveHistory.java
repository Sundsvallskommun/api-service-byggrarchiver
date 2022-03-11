package se.sundsvall.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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
    private String caseId;

    @Getter
    @Setter
    private String documentName;

    @Getter
    @Setter
    private String documentType;

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
    @Schema(readOnly = true)
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Getter
    @Setter
    @NotNull
    @ManyToOne
    private BatchHistory batchHistory;

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
                ", archiveId='" + archiveId + '\'' +
                ", archiveUrl='" + archiveUrl + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", batchHistory=" + batchHistory +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchiveHistory that = (ArchiveHistory) o;
        return Objects.equals(documentId, that.documentId) && systemType == that.systemType && Objects.equals(archiveId, that.archiveId) && Objects.equals(archiveUrl, that.archiveUrl) && status == that.status && Objects.equals(timestamp, that.timestamp) && Objects.equals(batchHistory, that.batchHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, systemType, archiveId, archiveUrl, status, timestamp, batchHistory);
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
