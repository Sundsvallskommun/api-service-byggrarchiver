package se.sundsvall;

public final class Constants {
    private Constants() {

    }

    ////////////////// RFC-url
    public static final String RFC_LINK_BAD_REQUEST = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.1";
    public static final String RFC_LINK_NOT_FOUND = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4";
    public static final String RFC_LINK_NOT_ALLOWED = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.5";
    public static final String RFC_LINK_INTERNAL_SERVER_ERROR = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.6.1";

    ////////////////// Error messages
    public static final String ERR_MSG_UNHANDLED_EXCEPTION = "An unhandled exception occurred. Contact the person responsible for the application. More information is provided in the log.";
    public static final String ERR_MSG_EXTERNAL_SERVICE = "Something went wrong in the request to an external service.";
}
