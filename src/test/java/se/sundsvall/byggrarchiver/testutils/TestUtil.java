package se.sundsvall.byggrarchiver.testutils;

import org.apache.commons.lang3.RandomStringUtils;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class TestUtil {
    public static ArchiveHistory createRandomArchiveHistory() {
        return ArchiveHistory.builder()
                .archiveId(UUID.randomUUID().toString())
                .archiveUrl("https://random-url")
                .batchHistory(createRandomBatchHistory())
                .archiveStatus((ArchiveStatus) getRandomOfEnum(ArchiveStatus.class))
                .documentId(UUID.randomUUID().toString())
                .documentName(getRandomString(20))
                .documentType(getRandomString(20))
                .caseId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static BatchHistory createRandomBatchHistory() {
        return BatchHistory.builder()
                .id(new Random().nextLong())
                .batchTrigger((BatchTrigger) getRandomOfEnum(BatchTrigger.class))
                .start(LocalDate.now())
                .end(LocalDate.now())
                .timestamp(LocalDateTime.now())
                .archiveStatus((ArchiveStatus) getRandomOfEnum(ArchiveStatus.class))
                .build();
    }

    public static <E extends Enum<E>> Enum<?> getRandomOfEnum(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).toList().get(new Random().nextInt(enumClass.getEnumConstants().length));
    }

    public static String getRandomString(int numberOfLetters) {
        return RandomStringUtils.random(numberOfLetters, true, false);
    }
}
