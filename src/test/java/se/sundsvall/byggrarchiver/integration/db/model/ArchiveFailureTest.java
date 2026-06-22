package se.sundsvall.byggrarchiver.integration.db.model;

import java.time.LocalDateTime;
import java.time.Month;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.byggrarchiver.api.model.enums.FailureCategory;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

class ArchiveFailureTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0).plusDays(randomInt()), LocalDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(ArchiveFailure.class, allOf(
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
		final var batchHistoryId = 2L;
		final var caseId = "caseId";
		final var documentId = "documentId";
		final var municipalityId = "2281";
		final var documentName = "documentName";
		final var failureCategory = FailureCategory.ARCHIVE_ERROR;
		final var message = "message";
		final var detail = "detail";
		final var timestamp = LocalDateTime.of(2024, Month.JANUARY, 16, 12, 0);

		// Act
		final var archiveFailure = ArchiveFailure.builder()
			.withId(id)
			.withBatchHistoryId(batchHistoryId)
			.withCaseId(caseId)
			.withDocumentId(documentId)
			.withMunicipalityId(municipalityId)
			.withDocumentName(documentName)
			.withFailureCategory(failureCategory)
			.withMessage(message)
			.withDetail(detail)
			.withTimestamp(timestamp)
			.build();

		// Assert
		assertThat(archiveFailure).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(archiveFailure.getId()).isEqualTo(id);
		assertThat(archiveFailure.getBatchHistoryId()).isEqualTo(batchHistoryId);
		assertThat(archiveFailure.getCaseId()).isEqualTo(caseId);
		assertThat(archiveFailure.getDocumentId()).isEqualTo(documentId);
		assertThat(archiveFailure.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(archiveFailure.getDocumentName()).isEqualTo(documentName);
		assertThat(archiveFailure.getFailureCategory()).isEqualTo(failureCategory);
		assertThat(archiveFailure.getMessage()).isEqualTo(message);
		assertThat(archiveFailure.getDetail()).isEqualTo(detail);
		assertThat(archiveFailure.getTimestamp()).isEqualTo(timestamp);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ArchiveFailure.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new ArchiveFailure()).hasAllNullFieldsOrProperties();
	}

}
