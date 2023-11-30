package se.sundsvall.byggrarchiver.service.mapper;

import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.DokumentFil;
import generated.se.sundsvall.arendeexport.Handling;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FASSIT2;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.SCHEDULED;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createBatchHistory;

class ArchiverMapperTest {

	@Test
	void testToArchiveHistory() throws Exception {
		final var handling = new Handling()
			.withHandlingDatum(LocalDate.of(2023, 1, 1))
			.withAnteckning("anteckning")
			.withDokument(new Dokument().withDokId("dokId").withNamn("namn").withFil(new DokumentFil().withFilBuffer(new byte[] { 1, 2, 3 }).withFilAndelse("pdf")));

		final var batchHistory = createBatchHistory(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), SCHEDULED, COMPLETED);

		final var archiveHistory = ArchiverMapper.toArchiveHistory(handling, batchHistory, "caseId", FASSIT2, COMPLETED);

		assertThat(archiveHistory.getArchiveStatus()).isEqualTo(COMPLETED);
		assertThat(archiveHistory.getBatchHistory()).isEqualTo(batchHistory);
		assertThat(archiveHistory.getCaseId()).isEqualTo("caseId");
		assertThat(archiveHistory.getDocumentId()).isEqualTo("dokId");
		assertThat(archiveHistory.getDocumentType()).isEqualTo(FASSIT2.getDescription());
	}


}
