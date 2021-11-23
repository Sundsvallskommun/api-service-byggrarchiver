package se.sundsvall.casemanagement;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Kategorisering av bilagor. " +

        "<br><br><h3>Bygglovsärende-kategorier:</h3>"
        + "<li>ANSOKAN_OM_BYGGLOV(Ansökan om bygglov)</li>"
        + "<li>GENERAL_ARRANGEMENT(Situationsplan)</li>"
        + "<li>APPLICATION(Anmälan)</li>"
        + "<li>REPORT_OF_CONTROL_OFFICIAL(Anmälan av kontrollansvarig)</li>"
        + "<li>CONSTRUCTION_DOCUMENT(Konstruktionshandling)</li>"
        + "<li>FLOOR_PLAN(Planritning)</li>"
        + "<li>ELEVATION(Fasadritning)</li>"
        + "<li>PLAN_FACADE_DESIGN(Plan- och fasadritning)</li>"
        + "<li>PROPOSED_CONTROL_PLAN(Förslag till kontrollplan)</li>"
        + "<li>NEW_CONSTRUCTION_MAP(Nybyggnadskarta)</li>"
        + "<li>SECTION_DESIGN(Sektionsritning)</li>"
        + "<li>CONSTRUCTION_DESIGN(Konstruktionsritning)</li>"
        + "<li>NEIGHBOR_CONSENT(Grannemedgivande)</li>"
        + "<li>DECLARATION_OF_PERFORMANCE(Prestandadeklaration)</li>"
        + "<li>PROXY(Fullmakt)</li>"
        + "<li>DESIGN(Ritning)</li>"
        + "<li>FIRE_PROTECTION_DOCUMENT(Brandskyddsdokumentation)</li>"
        + "<li>ATTACHMENT(Övriga bilagor)</li>"

        + "<br><h3>Miljökontorärende-kategorier:</h3>"
        + "<li>ANMALAN_LIVSMEDELSANLAGGNING(Anmälan livsmedelsanläggning)</li>")
public enum AttachmentCategory {

    ///////////////////////////////////
    // ByggR
    ///////////////////////////////////
    // Ansökan om bygglov
    ANSOKAN_OM_BYGGLOV("ANS"),
    // Situationsplan
    GENERAL_ARRANGEMENT("SITU"),
    // Anmälan
    APPLICATION("ANM"),
    // Anmälan av kontrollansvarig
    REPORT_OF_CONTROL_OFFICIAL("ANMÄ"),
    // Konstruktionshandling
    CONSTRUCTION_DOCUMENT("UKON"),
    // Planritning
    FLOOR_PLAN("PLA2"),
    // Fasadritning
    ELEVATION("FAS2"),
    // Plan- och fasadritning
    PLAN_FACADE_DESIGN("PLFA2"),
    // Förslag till kontrollplan
    PROPOSED_CONTROL_PLAN("FÖRK"),
    // Nybyggnadskarta
    NEW_CONSTRUCTION_MAP("NYKA"),
    // Sektionsritning
    SECTION_DESIGN("SEK2"),
    // Konstruktionsritning
    CONSTRUCTION_DESIGN("KONR"),
    // Grannemedgivande
    NEIGHBOR_CONSENT("GRAM"),
    // Prestandadeklaration
    DECLARATION_OF_PERFORMANCE("PRES"),
    // Fullmakt
    PROXY("FUM"),
    // Ritning
    DESIGN("RITNING"),
    // Brandskyddsdokumentation
    FIRE_PROTECTION_DOCUMENT("BRAD"),
    // Bilagor (övrigt)
    ATTACHMENT("BIL"),

    ///////////////////////////////////
    // Ecos
    ///////////////////////////////////
    // e-tjänsten: Livsmedelsverksamhet - anmälan om registrering
    ANMALAN_LIVSMEDELSANLAGGNING("Anmälan livsmedelsanläggning");

    private final String text;

    AttachmentCategory(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static AttachmentCategory valueOfText(String text) {
        for (AttachmentCategory attachmentCategory : values()) {
            if (attachmentCategory.text.equals(text)) {
                return attachmentCategory;
            }
        }
        throw new IllegalArgumentException("Could not find any AttachmentCategory with the text: " + text);
    }
}
