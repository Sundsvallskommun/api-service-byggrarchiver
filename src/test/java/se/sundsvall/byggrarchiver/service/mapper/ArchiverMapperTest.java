package se.sundsvall.byggrarchiver.service.mapper;

import generated.se.sundsvall.arendeexport.AbstractArendeObjekt;
import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.ArendePrelFastighet;
import generated.se.sundsvall.arendeexport.ArrayOfAbstractArendeObjekt2;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.DokumentFil;
import generated.se.sundsvall.arendeexport.Handling;
import generated.se.sundsvall.bygglov.ArkivobjektHandlingTyp;
import generated.se.sundsvall.bygglov.ArkivobjektListaHandlingarTyp;
import generated.se.sundsvall.bygglov.BilagaTyp;
import generated.se.sundsvall.bygglov.ExtraID;
import generated.se.sundsvall.bygglov.StatusArande;
import org.junit.jupiter.api.Test;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.BIL;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FAS;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FASSIT2;
import static se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger.SCHEDULED;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createBatchHistory;

class ArchiverMapperTest {

	@Test
	void testToArchiveHistory() {
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

	@Test
	void testToArchiveHistoryWithNull() {

		final var archiveHistory = ArchiverMapper.toArchiveHistory(null, null, null, null, null);

		assertThat(archiveHistory.getArchiveStatus()).isNull();
		assertThat(archiveHistory.getBatchHistory()).isNull();
		assertThat(archiveHistory.getCaseId()).isNull();
		assertThat(archiveHistory.getDocumentId()).isNull();
		assertThat(archiveHistory.getDocumentType()).isNull();
	}

	@Test
	void testToBilaga() throws Exception {
		final var dokument = new Dokument().withDokId("dokId")
			.withNamn("namn")
			.withFil(new DokumentFil()
				.withFilBuffer(new byte[] { 1, 2, 3 })
				.withFilAndelse("pdf"))
			.withBeskrivning("beskrivning");

		final var bilaga = ArchiverMapper.toBilaga(dokument);

		assertThat(bilaga.getNamn()).isEqualTo("namn.pdf");
		assertThat(bilaga.getBeskrivning()).isEqualTo("beskrivning");
		assertThat(bilaga.getLank()).isEqualTo("Bilagor\\namn.pdf");

	}

	@Test
	void testToBilagaNameWithExtension() throws Exception {
		final var dokument = new Dokument().withDokId("dokId")
			.withNamn("1_NameWithExtension.docx")
			.withFil(new DokumentFil()
				.withFilBuffer(new byte[] { 1, 2, 3 })
				.withFilAndelse("notUsed"))
			.withBeskrivning("beskrivning");

		final var bilaga = ArchiverMapper.toBilaga(dokument);

		assertThat(bilaga.getNamn()).isEqualTo("1_NameWithExtension.docx");
		assertThat(bilaga.getBeskrivning()).isEqualTo("beskrivning");
		assertThat(bilaga.getLank()).isEqualTo("Bilagor\\1_NameWithExtension.docx");

	}

	@Test
	void testToBilagaWhenNoExtension() throws Exception {
		final var dokument = new Dokument().withDokId("dokId")
			.withNamn("namn")
			.withFil(new DokumentFil()
				.withFilBuffer(Files.readAllBytes(Paths.get("src/test/resources/File_Without_Extension"))))
			.withBeskrivning("beskrivning");

		final var bilaga = ArchiverMapper.toBilaga(dokument);

		assertThat(bilaga.getNamn()).isEqualTo("namn.docx");
		assertThat(bilaga.getBeskrivning()).isEqualTo("beskrivning");
		assertThat(bilaga.getLank()).isEqualTo("Bilagor\\namn.docx");

	}

	@Test
	void testToBilagaWhenNoExtensionAndError() throws Exception {
		final var dokument = new Dokument().withDokId("dokId")
			.withNamn("namn")
			.withFil(new DokumentFil()
				.withFilBuffer(Files.readAllBytes(Paths.get("src/test/resources/Error_File_Without_Extension"))))
			.withBeskrivning("beskrivning");

		final var exception = assertThrows(ApplicationException.class, ()  -> ArchiverMapper.toBilaga(dokument));

		assertThat(exception.getMessage()).isEqualTo("Could not guess extension from bytearray");
	}

	@Test
	void testToBilagaWithDotInName() throws Exception {
		final var dokument = new Dokument().withDokId("dokId")
			.withNamn("namn.pdf")
			.withFil(new DokumentFil()
				.withFilBuffer(new byte[] { 1, 2, 3 })
				.withFilAndelse("pdf"))
			.withBeskrivning("beskrivning");

		final var bilaga = ArchiverMapper.toBilaga(dokument);

		assertThat(bilaga.getNamn()).isEqualTo("namn.pdf");
		assertThat(bilaga.getBeskrivning()).isEqualTo("beskrivning");
		assertThat(bilaga.getLank()).isEqualTo("Bilagor\\namn.pdf");

	}

	@Test
	void testToArendeFastighetList() {
		final List<AbstractArendeObjekt> abstractArendeObjektList = List.of(
				new ArendeFastighet().withArendeObjektId(1),
				new ArendePrelFastighet().withArendeObjektId(2));

		final var arendeFastighetList = ArchiverMapper.toArendeFastighetList(abstractArendeObjektList);

		assertThat(arendeFastighetList).hasSize(1);
		assertThat(arendeFastighetList.get(0).getArendeObjektId()).isEqualTo(1);
	}

	@Test
	void testToArkivbildarStrukturAfter2016() {
		final var arkivbildarStruktur = ArchiverMapper.toArkivbildarStruktur(LocalDate.now());

		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getNamn()).isEqualTo("Stadsbyggnadsnämnden");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidFran()).isEqualTo("2017");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidTill()).isNull();
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getArkivbildare()).isNull();
		assertThat(arkivbildarStruktur.getArkivbildare().getNamn()).isEqualTo("Sundsvalls kommun");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidFran()).isEqualTo("1974");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidTill()).isNull();
	}

	@Test
	void testToArkivbildarStrukturBefore1993() {
		final var arkivbildarStruktur = ArchiverMapper.toArkivbildarStruktur(LocalDate.of(1992, 12, 31));

		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getNamn()).isEqualTo("Byggnadsnämnden");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidFran()).isEqualTo("1974");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidTill()).isEqualTo("1992");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getArkivbildare()).isNull();
		assertThat(arkivbildarStruktur.getArkivbildare().getNamn()).isEqualTo("Sundsvalls kommun");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidFran()).isEqualTo("1974");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidTill()).isNull();
	}

	@Test
	void testToArkivbildarStrukturBetween1993And2016() {
		final var arkivbildarStruktur = ArchiverMapper.toArkivbildarStruktur(LocalDate.of(1993, 1, 1));

		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getNamn()).isEqualTo("Stadsbyggnadsnämnden");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidFran()).isEqualTo("1993");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidTill()).isEqualTo("2017");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getArkivbildare()).isNull();
		assertThat(arkivbildarStruktur.getArkivbildare().getNamn()).isEqualTo("Sundsvalls kommun");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidFran()).isEqualTo("1974");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidTill()).isNull();
	}

	@Test
	void testToArkivbildarStruktur1992() {
		final var arkivbildarStruktur = ArchiverMapper.toArkivbildarStruktur(LocalDate.of(1992, 12, 31));

		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getNamn()).isEqualTo("Byggnadsnämnden");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidFran()).isEqualTo("1974");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getVerksamhetstidTill()).isEqualTo("1992");
		assertThat(arkivbildarStruktur.getArkivbildare().getArkivbildare().getArkivbildare()).isNull();
		assertThat(arkivbildarStruktur.getArkivbildare().getNamn()).isEqualTo("Sundsvalls kommun");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidFran()).isEqualTo("1974");
		assertThat(arkivbildarStruktur.getArkivbildare().getVerksamhetstidTill()).isNull();
	}

	@Test
	void testToIsoDateLocalDate() {
		assertThat(ArchiverMapper.toIsoDate(LocalDate.of(2023, 1, 1))).isEqualTo("2023-01-01");
		assertThat(ArchiverMapper.toIsoDate((LocalDate) null)).isNull();
	}

	@Test
	void testToIsoDateLocalDateTime() {
		assertThat(ArchiverMapper.toIsoDate(LocalDate.of(2023, 1, 1).atStartOfDay())).isEqualTo("2023-01-01");
		assertThat(ArchiverMapper.toIsoDate((LocalDateTime) null)).isNull();
	}

	@Test
	void testToAttachmentCategory() {
		assertThat(ArchiverMapper.toAttachmentCategory(FAS.toString())).isEqualTo(FAS);
		assertThat(ArchiverMapper.toAttachmentCategory("NonExistingCategory")).isEqualTo(BIL);
	}

	@Test
	void testToArkivobjektArendeTyp() throws Exception {
		// Arrange
		final var arende = new Arende2()
			.withArendeId(1)
			.withDnr("arendeId")
			.withArendetyp("arendeTyp")
			.withAnkomstDatum(LocalDate.of(2023, 1, 1))
			.withRegistreradDatum(LocalDate.of(2022, 1, 2))
			.withSlutDatum(LocalDate.of(2023, 1, 3))
			.withBeskrivning("beskrivning")
			.withObjektLista(new ArrayOfAbstractArendeObjekt2()
				.withAbstractArendeObjekt(new ArendeFastighet().withArendeObjektId(1))
				.withAbstractArendeObjekt(new ArendePrelFastighet().withArendeObjektId(2)))
			.withStatus("Stängt");

		final var handling = new Handling()
			.withHandlingDatum(LocalDate.of(2023, 1, 1))
			.withAnteckning("anteckning")
			.withTyp("handlingstyp")
			.withDokument(new Dokument().withDokId("dokId")
				.withNamn("namn")
				.withFil(new DokumentFil().withFilBuffer(new byte[] { 1, 2, 3 })
					.withFilAndelse("pdf")));

		final var document = new Dokument().withDokId("dokId")
			.withNamn("namn")
			.withFil(new DokumentFil()
				.withFilBuffer(new byte[] { 1, 2, 3 })
				.withFilAndelse("pdf"))
			.withBeskrivning("beskrivning");

		var arkivobjektHandling = new ArkivobjektHandlingTyp();
		arkivobjektHandling.setArkivobjektID(document.getDokId());
		arkivobjektHandling.setSkapad("2023-01-01");
		arkivobjektHandling.getBilaga().add(new BilagaTyp());
		arkivobjektHandling.setHandlingstyp("handlingstyp");

		var arkivobjektListaHandlingarTyp = new ArkivobjektListaHandlingarTyp();
		arkivobjektListaHandlingarTyp.getArkivobjektHandling().add(arkivobjektHandling);

		final var expectedExtraID = new ExtraID();
		expectedExtraID.setContent("arendeId");
		final var expectedStatusArende = new StatusArande();
		expectedStatusArende.setValue("Stängt");

		// Act
		final var arkivobjektArendeTyp = ArchiverMapper.toArkivobjektArendeTyp(arende, handling, document);

		// Assert
		assertThat(arkivobjektArendeTyp.getArkivobjektID()).isEqualTo("arendeId");
		assertThat(arkivobjektArendeTyp.getExtraID()).hasSize(1);
		assertThat(arkivobjektArendeTyp.getExtraID().get(0)).usingRecursiveComparison().isEqualTo(expectedExtraID);
		assertThat(arkivobjektArendeTyp.getArendeTyp()).isEqualTo("arendeTyp");
		assertThat(arkivobjektArendeTyp.getArendemening()).isEqualTo("beskrivning");
		assertThat(arkivobjektArendeTyp.getAvslutat()).isEqualTo("2023-01-03");
		assertThat(arkivobjektArendeTyp.getSkapad()).isEqualTo("2022-01-02");
		assertThat(arkivobjektArendeTyp.getStatusArande()).usingRecursiveComparison().isEqualTo(expectedStatusArende);
		assertThat(arkivobjektArendeTyp.getArkivobjektListaHandlingar().getArkivobjektHandling()).hasSize(1);
	}

	@Test
	void testToByggRArchiveRequest() throws Exception {
		// Arrange
		final var dokument = new Dokument().withDokId("dokId")
			.withNamn("namn")
			.withFil(new DokumentFil()
				.withFilBuffer(new byte[] { 1, 2, 3 })
				.withFilAndelse("pdf"))
			.withBeskrivning("beskrivning");

		// Act
		final var byggRArchiveRequest = ArchiverMapper.toByggRArchiveRequest(dokument, "metaData");

		// Assert
		assertThat(byggRArchiveRequest.getAttachment().getExtension()).isEqualTo(".pdf");
		assertThat(byggRArchiveRequest.getAttachment().getName()).isEqualTo("namn.pdf");
		assertThat(byggRArchiveRequest.getAttachment().getFile()).isEqualTo("AQID");
		assertThat(byggRArchiveRequest.getMetadata()).isEqualTo("metaData");
	}

}
