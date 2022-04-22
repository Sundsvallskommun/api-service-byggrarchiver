package se.sundsvall.byggrarchiver.service.util;

public final class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String LANTMATERIET_HTML_TEMPLATE = "<p>Hej!</p>" +
            "<p>En geoteknisk handling har precis blivit arkiverad. Handlingen finns bifogad i mailet.<br />" +
            "Denna ska l&auml;ggas till p&aring; <a href=\"https://karta.sundsvall.se/\">https://karta.sundsvall.se/</a></p>" +
            "<ul>" +
            "<li><strong>URL till arkivet:</strong> <a href=\"${archiveUrl}\">Long-Term Archive(LTA)</a></li>" +
            "<li><strong>&Auml;rende-ID i Byggr:</strong> ${byggrCaseId}</li>" +
            "<li><strong>Namn p&aring; handlingen i Byggr:</strong> ${byggrDocumentName}</li>" +
            "</ul>" +
            "<p>Vid eventuella problem, svara p&aring; detta mail.</p>" +
            "<p>Mvh<br />ByggrArchiver</p>";

    public static final String EXTENSION_ERROR_HTML_TEMPLATE = "<p>Hej!</p>" +
            "<p>N&auml;r ett dokument skulle arkiveras i LTA s&aring; uppstod ett problem.<br />" +
            "Filtypen p&aring; dokumentet st&ouml;ds inte. Konvertera dokumentet till n&aring;got av de till&aring;tna filformaten:</p>" +
            "<ul>" +
            "<li>.pdf</li>" +
            "<li>.docx</li>" +
            "<li>.doc</li>" +
            "<li>.msg</li>" +
            "<li>.xlsx</li>" +
            "<li>.tif</li>" +
            "<li>.pptx</li>" +
            "<li>.png</li>" +
            "</ul>" +
            "<p>Uppgifter om dokumentet:</p>" +
            "<ul>" +
            "<li><strong>Ligger i &auml;rende:</strong> ${byggrCaseId}</li>" +
            "<li><strong>Dokumentnamn:</strong> ${documentName}</li>" +
            "<li><strong>Handlingstyp:</strong> ${documentType}</li>" +
            "</ul>" +
            "<p>Svara på detta mail när ni har konverterat dokumentet till rätt format så vi kan slutföra arkiveringen.</p>" +
            "<p>Mvh<br />ByggrArchiver</p>";
    
    public static final String ARCHIVE_URL_QUERY = "/Search?searchPath=Bygglovshandlingar%20AGS&aipFilterOption=0&%C3%84rendenummer=MatchesPhrase(${byggrCaseId})&Handlingsnummer=MatchesPhrase(${byggrDocumentId})";

    ////////////////// RFC-url
    public static final String RFC_LINK_BAD_REQUEST = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.1";
    public static final String RFC_LINK_NOT_FOUND = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4";
    public static final String RFC_LINK_NOT_ALLOWED = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.5";
    public static final String RFC_LINK_INTERNAL_SERVER_ERROR = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.6.1";
    public static final String RFC_LINK_SERVICE_UNAVAILABLE = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.6.4";
    public static final String RFC_LINK_NOT_IMPLEMENTED = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.6.2";

    ////////////////// Error messages
    public static final String ERR_MSG_UNHANDLED_EXCEPTION = "An unhandled exception occurred. Contact the person responsible for the application. More information is provided in the log.";
    public static final String ERR_MSG_EXTERNAL_SERVICE = "Error in external service.";
    public static final String IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH = "It's not possible to rerun a completed batch.";
    public static final String END_CAN_NOT_BE_BEFORE_START = "End can not be before start";

    public static final String BYGGR_STATUS_AVSLUTAT = "Avslutat";
    public static final String BYGGR_HANDELSETYP_ARKIV = "ARKIV";
}
