package se.sundsvall.byggrarchiver.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;

class BatchHistoryTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDateTime.now().plusDays(randomInt()), LocalDateTime.class);
		registerValueGenerator(() -> LocalDate.now().plusDays(randomInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(BatchHistory.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var id = 1L;
		final var start = LocalDate.now();
		final var end = LocalDate.now();
		final var archiveStatus = ArchiveStatus.COMPLETED;
		final var batchTrigger = BatchTrigger.MANUAL;
		final var timestamp = LocalDateTime.now();
		final var municipalityId = "2281";

		// Act
		final var batchHistory = BatchHistory.builder()
			.withId(id)
			.withStart(start)
			.withEnd(end)
			.withArchiveStatus(archiveStatus)
			.withBatchTrigger(batchTrigger)
			.withMunicipalityId(municipalityId)
			.withTimestamp(timestamp)
			.build();

		// Assert
		assertThat(batchHistory).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(batchHistory.getId()).isEqualTo(id);
		assertThat(batchHistory.getStart()).isEqualTo(start);
		assertThat(batchHistory.getEnd()).isEqualTo(end);
		assertThat(batchHistory.getArchiveStatus()).isEqualTo(archiveStatus);
		assertThat(batchHistory.getBatchTrigger()).isEqualTo(batchTrigger);
		assertThat(batchHistory.getTimestamp()).isEqualTo(timestamp);
		assertThat(batchHistory.getMunicipalityId()).isEqualTo(municipalityId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(BatchHistory.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new BatchHistory()).hasAllNullFieldsOrProperties();
	}

}
