package se.sundsvall.byggrarchiver.service.util;

public final class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BATCH_HISTORY_NOT_FOUND = "BatchHistory not found";
    public static final String ARCHIVE_HISTORY_NOT_FOUND = "ArchiveHistory not found";

    public static final String ARENDEEXPORT_ERROR_MESSAGE = "Request to arendeExportIntegrationService failed.";

    public static final String ARCHIVE_URL_QUERY = "/Search?searchPath=AGS%20Bygglov&aipFilterOption=0&Arkivpakets-ID=MatchesPhrase(${archiveId})";

    public static final String IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH = "It's not possible to rerun a completed batch.";
    public static final String END_CAN_NOT_BE_BEFORE_START = "End can not be before start";

    public static final String BYGGR_STATUS_AVSLUTAT = "Avslutat";
    public static final String BYGGR_HANDELSETYP_ARKIV = "ARKIV";

    public static final String F_2_BYGGLOV = "F2 Bygglov";
    public static final String HANTERA_BYGGLOV = "3.1.4.1 Hantera Bygglov";
    public static final String SUNDSVALLS_KOMMUN = "Sundsvalls kommun";
    public static final String BYGGNADSNAMNDEN = "Byggnadsnämnden";
    public static final String STADSBYGGNADSNAMNDEN = "Stadsbyggnadsnämnden";
}
