package se.sundsvall.byggrarchiver.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;

class ArchiveHistoryResponseTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(randomInt()), LocalDate.class);
		registerValueGenerator(() -> LocalDateTime.now().plusDays(randomInt()), LocalDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(ArchiveHistoryResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var documentId = "documentId";
		final var caseId = "caseId";
		final var documentName = "documentName";
		final var documentType = "documentType";
		final var archiveId = "archiveId";
		final var archiveUrl = "archiveUrl";
		final var archiveStatus = ArchiveStatus.COMPLETED;
		final var timestamp = LocalDateTime.now();
		final var batchHistory = new BatchHistoryResponse();

		// Act
		final var archiveHistoryResponse = ArchiveHistoryResponse.builder()
			.withDocumentId(documentId)
			.withCaseId(caseId)
			.withDocumentName(documentName)
			.withDocumentType(documentType)
			.withArchiveId(archiveId)
			.withArchiveUrl(archiveUrl)
			.withArchiveStatus(archiveStatus)
			.withTimestamp(timestamp)
			.withBatchHistory(batchHistory)
			.build();

		// Assert
		assertThat(archiveHistoryResponse).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(archiveHistoryResponse.getDocumentId()).isEqualTo(documentId);
		assertThat(archiveHistoryResponse.getCaseId()).isEqualTo(caseId);
		assertThat(archiveHistoryResponse.getDocumentName()).isEqualTo(documentName);
		assertThat(archiveHistoryResponse.getDocumentType()).isEqualTo(documentType);
		assertThat(archiveHistoryResponse.getArchiveId()).isEqualTo(archiveId);
		assertThat(archiveHistoryResponse.getArchiveUrl()).isEqualTo(archiveUrl);
		assertThat(archiveHistoryResponse.getArchiveStatus()).isEqualTo(archiveStatus);
		assertThat(archiveHistoryResponse.getTimestamp()).isEqualTo(timestamp);
		assertThat(archiveHistoryResponse.getBatchHistory()).isEqualTo(batchHistory);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ArchiveHistoryResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new ArchiveHistoryResponse()).hasAllNullFieldsOrProperties();
	}

}
