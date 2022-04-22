package se.sundsvall.byggrarchiver.integration.db.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import se.sundsvall.byggrarchiver.api.model.Status;

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
    @Column( length = 1000 )
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
        final StringBuilder sb = new StringBuilder("ArchiveHistory{");
        sb.append("documentId='").append(documentId).append('\'');
        sb.append(", caseId='").append(caseId).append('\'');
        sb.append(", documentName='").append(documentName).append('\'');
        sb.append(", documentType='").append(documentType).append('\'');
        sb.append(", archiveId='").append(archiveId).append('\'');
        sb.append(", archiveUrl='").append(archiveUrl).append('\'');
        sb.append(", status=").append(status);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", batchHistory=").append(batchHistory);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchiveHistory that = (ArchiveHistory) o;
        return Objects.equals(documentId, that.documentId) && Objects.equals(caseId, that.caseId) && Objects.equals(documentName, that.documentName) && Objects.equals(documentType, that.documentType) && Objects.equals(archiveId, that.archiveId) && Objects.equals(archiveUrl, that.archiveUrl) && status == that.status && Objects.equals(timestamp, that.timestamp) && Objects.equals(batchHistory, that.batchHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, caseId, documentName, documentType, archiveId, archiveUrl, status, timestamp, batchHistory);
    }
}

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
class IdPk implements Serializable {

    private String documentId;
    private String caseId;
}
