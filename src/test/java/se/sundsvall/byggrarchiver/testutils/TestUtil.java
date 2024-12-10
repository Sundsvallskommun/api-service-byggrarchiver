package se.sundsvall.byggrarchiver.testutils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import se.sundsvall.byggrarchiver.api.model.BatchHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;

public final class TestUtil {

	private TestUtil() {}

	public static ArchiveHistory createRandomArchiveHistory() {
		return ArchiveHistory.builder()
			.withArchiveId(UUID.randomUUID().toString())
			.withArchiveUrl("https://random-url")
			.withBatchHistory(createRandomBatchHistory())
			.withArchiveStatus(getRandomEnumValue(ArchiveStatus.class))
			.withDocumentId(UUID.randomUUID().toString())
			.withDocumentName(UUID.randomUUID().toString().substring(0, 21))
			.withDocumentType(UUID.randomUUID().toString().substring(0, 21))
			.withCaseId(UUID.randomUUID().toString())
			.withTimestamp(LocalDateTime.now())
			.build();
	}

	public static BatchHistory createRandomBatchHistory() {
		return BatchHistory.builder()
			.withId(randomLong())
			.withBatchTrigger(getRandomEnumValue(BatchTrigger.class))
			.withStart(LocalDate.now())
			.withEnd(LocalDate.now())
			.withTimestamp(LocalDateTime.now())
			.withArchiveStatus(getRandomEnumValue(ArchiveStatus.class))
			.build();
	}

	public static BatchHistoryResponse createRandomBatchHistoryResponse() {
		return BatchHistoryResponse.builder()
			.withId(randomLong())
			.withBatchTrigger(getRandomEnumValue(BatchTrigger.class))
			.withStart(LocalDate.now())
			.withEnd(LocalDate.now())
			.withTimestamp(LocalDateTime.now())
			.withArchiveStatus(getRandomEnumValue(ArchiveStatus.class))
			.build();
	}

	public static int randomInt() {
		return randomInt(Integer.MAX_VALUE);
	}

	public static int randomInt(final int max) {
		return ThreadLocalRandom.current().nextInt(max);
	}

	public static long randomLong() {
		return randomLong(Long.MAX_VALUE);
	}

	public static long randomLong(final long max) {
		return ThreadLocalRandom.current().nextLong(max);
	}

	public static <E extends Enum<E>> E getRandomEnumValue(final Class<E> enumClass) {
		return enumClass.getEnumConstants()[randomInt(enumClass.getEnumConstants().length)];
	}

	public static BatchHistory createBatchHistory(final LocalDate start, final LocalDate end, final BatchTrigger batchTrigger, final ArchiveStatus archiveStatus) {
		return BatchHistory.builder()
			.withStart(start)
			.withEnd(end)
			.withBatchTrigger(batchTrigger)
			.withArchiveStatus(archiveStatus).build();
	}

}
