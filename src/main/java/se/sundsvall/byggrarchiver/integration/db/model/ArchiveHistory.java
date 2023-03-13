package se.sundsvall.byggrarchiver.integration.db.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@IdClass(IdPk.class)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ArchiveHistory {
    @Id
    private String documentId;

    @Id
    private String caseId;

    private String documentName;

    private String documentType;

    private String archiveId;

    private String archiveUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArchiveStatus archiveStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @NotNull
    @ManyToOne
    private BatchHistory batchHistory;

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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchiveHistory that = (ArchiveHistory) o;
        return Objects.equals(documentId, that.documentId) && Objects.equals(caseId, that.caseId) && Objects.equals(documentName, that.documentName) && Objects.equals(documentType, that.documentType) && Objects.equals(archiveId, that.archiveId) && Objects.equals(archiveUrl, that.archiveUrl) && archiveStatus == that.archiveStatus && Objects.equals(timestamp, that.timestamp) && Objects.equals(batchHistory, that.batchHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, caseId, documentName, documentType, archiveId, archiveUrl, archiveStatus, timestamp, batchHistory);
    }
}

@Embeddable
class IdPk implements Serializable {

    private String documentId;
    private String caseId;
}
