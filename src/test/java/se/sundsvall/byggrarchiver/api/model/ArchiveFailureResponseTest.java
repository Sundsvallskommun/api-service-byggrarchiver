package se.sundsvall.byggrarchiver.api.model;

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
import static org.hamcrest.Matchers.allOf;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

class ArchiveFailureResponseTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0).plusDays(randomInt()), LocalDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(ArchiveFailureResponse.class, allOf(
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
		final var failureCategory = FailureCategory.BYGGR_FETCH_ERROR;
		final var message = "message";
		final var detail = "detail";
		final var timestamp = LocalDateTime.of(2024, Month.JANUARY, 16, 12, 0);

		// Act
		final var archiveFailureResponse = ArchiveFailureResponse.builder()
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
		assertThat(archiveFailureResponse).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(archiveFailureResponse.getId()).isEqualTo(id);
		assertThat(archiveFailureResponse.getBatchHistoryId()).isEqualTo(batchHistoryId);
		assertThat(archiveFailureResponse.getCaseId()).isEqualTo(caseId);
		assertThat(archiveFailureResponse.getDocumentId()).isEqualTo(documentId);
		assertThat(archiveFailureResponse.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(archiveFailureResponse.getDocumentName()).isEqualTo(documentName);
		assertThat(archiveFailureResponse.getFailureCategory()).isEqualTo(failureCategory);
		assertThat(archiveFailureResponse.getMessage()).isEqualTo(message);
		assertThat(archiveFailureResponse.getDetail()).isEqualTo(detail);
		assertThat(archiveFailureResponse.getTimestamp()).isEqualTo(timestamp);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ArchiveFailureResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new ArchiveFailureResponse()).hasAllNullFieldsOrProperties();
	}

}
