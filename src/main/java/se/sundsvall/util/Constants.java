package se.sundsvall.util;

public final class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String LANTMATERIET_HTML_TEMPLATE = "<p>Hej!</p>" +
            "<p>En geoteknisk handling har precis blivit arkiverad. Handlingen finns bifogad i mailet.<br />Denna ska l&auml;ggas till p&aring; <a href=\"https://karta.sundsvall.se/\">https://karta.sundsvall.se/</a></p>" +
            "<ul>" +
            "<li><strong>Arkiverings-ID:</strong> ${archiveId}</li>" +
            "<li><strong>URL till den arkiverade handlingen:</strong> <a href=\"${archiveUrl}\">${archiveUrl}/</a></li>" +
            "<li><strong>&Auml;rende-ID i Byggr:</strong> ${byggrCaseId}</li>" +
            "<li><strong>Namn p&aring; handlingen i Byggr:</strong> ${byggrDocumentName}</li>" +
            "<li><strong>Dokument-ID i Byggr: </strong>${byggrDocumentId}</li>" +
            "</ul>" +
            "<p>Vid eventuella problem, svara p&aring; detta mail.</p>" +
            "<p>Mvh<br />Archiver</p>";

    ////////////////// RFC-url
    public static final String RFC_LINK_BAD_REQUEST = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.1";
    public static final String RFC_LINK_NOT_FOUND = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4";
    public static final String RFC_LINK_NOT_ALLOWED = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.5";
    public static final String RFC_LINK_INTERNAL_SERVER_ERROR = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.6.1";

    ////////////////// Error messages
    public static final String ERR_MSG_UNHANDLED_EXCEPTION = "An unhandled exception occurred. Contact the person responsible for the application. More information is provided in the log.";
    public static final String ERR_MSG_EXTERNAL_SERVICE = "Something went wrong in the request to an external service.";
    public static final String IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH = "It's not possible to rerun a completed batch.";
    public static final String END_CAN_NOT_BE_BEFORE_START = "End can not be before start";

    public static final String LANTMATERIET_REFERENS_STATUS_GALLANDE = "Gällande";
    public static final String FB_DATABASE = "Standard";

    public static final String BYGGR_STATUS_AVSLUTAT = "Avslutat";
    public static final String BYGGR_HANDELSETYP_ARKIV = "ARKIV";
    public static final String BYGGR_HANDELSESLAG_SLUTBESKED = "SLU";
}
