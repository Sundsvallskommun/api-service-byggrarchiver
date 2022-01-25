package se.sundsvall.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class ArchiveMetadata {

    @Schema(description = "Dokument-ID från underliggande system")
    private String documentId;
    @Schema(description = "Tidpunkt då dokumentet skapades")
    private LocalDateTime documentCreatedAt;
    @Schema(description = "Underliggande system")
    private SystemType system;
    @Schema(description = "Klassning som används för att avgöra vilka som har behörighet att se den arkiverade bilagan")
    private String archiveClassification;
    @Schema(description = "Ärende-ID från underliggande system")
    private String caseId;
    @Schema(description = "Ärendemening")
    private String caseTitle;
    @Schema(description = "Tidpunkt då ärendet skapades")
    private LocalDate caseCreatedAt;
    @Schema(description = "Tidpunkt då ärendet avslutades")
    private LocalDate caseEndedAt;
    @Schema(description = "Tidpunkt då det togs ett slutbesked i ärendet")
    private LocalDateTime caseEndDecisionAt;
    @Schema(description = "Sekretess")
    private Boolean secrecy;

    // PropertyData
    private String propertyDesignation;
    private String region;
    @Schema(description = "Lanmäteriets registerenhet (GUID)")
    private String registerUnit;
}
