package se.sundsvall.byggrarchiver.integration.db.model;

import java.time.LocalDateTime;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

class ArchiveHistoryTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDateTime.now().plusDays(randomInt()), LocalDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(ArchiveHistory.class, allOf(
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
		final var batchHistory = new BatchHistory();
		final var municipalityId = "municipalityId";

		// Act
		final var archiveHistory = ArchiveHistory.builder()
			.withDocumentId(documentId)
			.withCaseId(caseId)
			.withDocumentName(documentName)
			.withDocumentType(documentType)
			.withArchiveId(archiveId)
			.withArchiveUrl(archiveUrl)
			.withArchiveStatus(archiveStatus)
			.withTimestamp(timestamp)
			.withBatchHistory(batchHistory)
			.withMunicipalityId(municipalityId)
			.build();

		// Assert
		assertThat(archiveHistory).hasNoNullFieldsOrProperties();
		assertThat(archiveHistory.getDocumentId()).isEqualTo(documentId);
		assertThat(archiveHistory.getCaseId()).isEqualTo(caseId);
		assertThat(archiveHistory.getDocumentName()).isEqualTo(documentName);
		assertThat(archiveHistory.getDocumentType()).isEqualTo(documentType);
		assertThat(archiveHistory.getArchiveId()).isEqualTo(archiveId);
		assertThat(archiveHistory.getArchiveUrl()).isEqualTo(archiveUrl);
		assertThat(archiveHistory.getArchiveStatus()).isEqualTo(archiveStatus);
		assertThat(archiveHistory.getTimestamp()).isEqualTo(timestamp);
		assertThat(archiveHistory.getBatchHistory()).isEqualTo(batchHistory);
		assertThat(archiveHistory.getMunicipalityId()).isEqualTo(municipalityId);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ArchiveHistory.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new ArchiveHistory()).hasAllNullFieldsOrProperties();
	}

}
