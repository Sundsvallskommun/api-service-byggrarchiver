package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

class BatchHistoryResponseTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(randomInt()), LocalDate.class);
		registerValueGenerator(() -> LocalDateTime.now().plusDays(randomInt()), LocalDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(BatchHistoryResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	void builder() {
		// Arrange
		final var id = 1L;
		final var start = LocalDate.now();
		final var end = LocalDate.now();
		final var archiveStatus = ArchiveStatus.COMPLETED;
		final var batchTrigger = BatchTrigger.MANUAL;
		final var timestamp = LocalDateTime.now();

		// Act
		final var batchHistoryResponse = BatchHistoryResponse.builder()
			.withId(id)
			.withStart(start)
			.withEnd(end)
			.withArchiveStatus(archiveStatus)
			.withBatchTrigger(batchTrigger)
			.withTimestamp(timestamp)
			.build();

		// Assert
		assertThat(batchHistoryResponse).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(batchHistoryResponse.getId()).isEqualTo(id);
		assertThat(batchHistoryResponse.getStart()).isEqualTo(start);
		assertThat(batchHistoryResponse.getEnd()).isEqualTo(end);
		assertThat(batchHistoryResponse.getArchiveStatus()).isEqualTo(archiveStatus);
		assertThat(batchHistoryResponse.getBatchTrigger()).isEqualTo(batchTrigger);
		assertThat(batchHistoryResponse.getTimestamp()).isEqualTo(timestamp);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(BatchHistoryResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new BatchHistoryResponse()).hasAllNullFieldsOrProperties();
	}

}
