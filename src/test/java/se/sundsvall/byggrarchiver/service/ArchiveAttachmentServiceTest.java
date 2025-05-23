package se.sundsvall.byggrarchiver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FASSIT2;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.PLFASE;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.TOMTPLBE;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.createRandomArchiveHistory;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;
import static se.sundsvall.byggrarchiver.util.Constants.F_2_BYGGLOV;
import static se.sundsvall.byggrarchiver.util.Constants.HANTERA_BYGGLOV;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import generated.se.sundsvall.arendeexport.Arende;
import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.ArrayOfAbstractArendeObjekt2;
import generated.se.sundsvall.arendeexport.ArrayOfHandelse;
import generated.se.sundsvall.arendeexport.ArrayOfHandelseHandling;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.DokumentFil;
import generated.se.sundsvall.arendeexport.Fastighet;
import generated.se.sundsvall.arendeexport.Handelse;
import generated.se.sundsvall.arendeexport.HandelseHandling;
import generated.se.sundsvall.bygglov.FastighetTyp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Status;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.configuration.LongTermArchiveProperties;
import se.sundsvall.byggrarchiver.integration.archive.ArchiveIntegration;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.dept44.exception.ClientProblem;

@ExtendWith(MockitoExtension.class)
class ArchiveAttachmentServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	LongTermArchiveProperties longTermArchivePropertiesMock;

	@Mock
	private ArchiveHistoryRepository archiveHistoryRepositoryMock;

	@Mock
	private MessagingIntegration messagingIntegrationMock;

	@Mock
	private ArchiveIntegration archiveIntegrationMock;

	@Mock
	private FbIntegration fastighetService;

	@Captor
	private ArgumentCaptor<ByggRArchiveRequest> byggRArchiveRequestCaptor;

	@InjectMocks
	private ArchiveAttachmentService archiveAttachmentService;

	private static Stream<Arguments> constructDocuments() {
		final var document1 = new Dokument();
		document1.setNamn("test.without.extension");
		document1.setFil(new DokumentFil());
		document1.getFil().setFilAndelse(".docx");
		final var document2 = new Dokument();
		document2.setNamn("test.without extension 2");
		document2.setFil(new DokumentFil());
		document2.getFil().setFilAndelse("pdf");

		final var document3 = new Dokument();
		final var dokumentFil = new DokumentFil();
		dokumentFil.setFilAndelse("pdf");
		document3.setFil(dokumentFil);
		document3.setNamn("test.with   .extension.DOCX");

		return Stream.of(
			// Sanity check passes
			Arguments.of(document1),
			// Sanity check passes
			Arguments.of(document2),
			Arguments.of(document3));
	}

	@Test
	void archive() throws Exception {
		// Arrange
		final var arende = createArendeObject(List.of(AttachmentCategory.ANS));
		final var handling = arende.getHandelseLista().getHandelse().getFirst().getHandlingLista().getHandling().getFirst();
		final var document = handling.getDokument();

		final var archiveResponse = new ArchiveResponse();
		archiveResponse.setArchiveId("123456");

		final var archiveHistory = createRandomArchiveHistory();

		when(archiveHistoryRepositoryMock.save(archiveHistory)).thenReturn(archiveHistory);
		when(archiveIntegrationMock.archive(byggRArchiveRequestCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(archiveResponse);
		when(fastighetService.getFastighet(any())).thenReturn(new FastighetTyp());

		final var result = archiveAttachmentService.archiveAttachment(arende, handling, document, archiveHistory, MUNICIPALITY_ID);

		// Assert and verify
		assertThat(result).isNotNull();
		assertThat(result.getArchiveId()).isEqualTo(archiveResponse.getArchiveId());
		assertThat(result.getArchiveStatus()).isEqualTo(ArchiveStatus.COMPLETED);
		assertThat(byggRArchiveRequestCaptor.getValue()).isNotNull();
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).isNotNull();
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).containsIgnoringWhitespaces("""
			<Arkivbildare>
				<Namn>Stadsbyggnadsnämnden</Namn>
				<VerksamhetstidFran>2017</VerksamhetstidFran>
			</Arkivbildare>""");
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).contains("<Namn>Stadsbyggnadsnämnden</Namn>");
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).contains("<Klass>" + HANTERA_BYGGLOV + "</Klass>");
		assertThat(byggRArchiveRequestCaptor.getValue().getAttachment()).isNotNull();
		assertThat(byggRArchiveRequestCaptor.getValue().getAttachment().getName()).isEqualTo("Test filnamn.pdf");
		assertThat(byggRArchiveRequestCaptor.getValue().getAttachment().getExtension()).isEqualTo(".pdf");

		verify(archiveHistoryRepositoryMock).save(any(ArchiveHistory.class));
		verify(archiveIntegrationMock).archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID));
		verify(fastighetService).getFastighet(any());
	}

	@Test
	void archiveWhenAnkomstDatumBefore2017() throws Exception {
		// Arrange
		final var arende = createArendeObject(List.of(AttachmentCategory.ANS)).withAnkomstDatum(LocalDate.of(2016, 12, 31));
		final var handling = arende.getHandelseLista().getHandelse().getFirst().getHandlingLista().getHandling().getFirst();
		final var document = handling.getDokument();
		final var archiveResponse = new ArchiveResponse();
		archiveResponse.setArchiveId("123456");

		final var archiveHistory = createRandomArchiveHistory();

		when(archiveHistoryRepositoryMock.save(archiveHistory)).thenReturn(archiveHistory);
		when(archiveIntegrationMock.archive(byggRArchiveRequestCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(archiveResponse);
		when(fastighetService.getFastighet(any())).thenReturn(new FastighetTyp());

		final var result = archiveAttachmentService.archiveAttachment(arende, handling, document, archiveHistory, MUNICIPALITY_ID);

		// Assert and verify
		assertThat(result).isNotNull();
		assertThat(result.getArchiveId()).isEqualTo(archiveResponse.getArchiveId());
		assertThat(result.getArchiveStatus()).isEqualTo(ArchiveStatus.COMPLETED);
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).containsIgnoringWhitespaces("""
																		<Arkivbildare>
				<Namn>Stadsbyggnadsnämnden</Namn>
				<VerksamhetstidFran>1993</VerksamhetstidFran>
				<VerksamhetstidTill>2017</VerksamhetstidTill>
			</Arkivbildare>""");
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).contains("<Klass>" + F_2_BYGGLOV + "</Klass>");
		verify(archiveHistoryRepositoryMock).save(any(ArchiveHistory.class));
		verify(archiveIntegrationMock).archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID));
		verify(fastighetService).getFastighet(any());
	}

	@Test
	void archiveWhenAnkomstDatumBefore1993() throws Exception {
		// Arrange
		final var arende = createArendeObject(List.of(AttachmentCategory.ANS)).withAnkomstDatum(LocalDate.of(1992, 12, 31));
		final var handling = arende.getHandelseLista().getHandelse().getFirst().getHandlingLista().getHandling().getFirst();
		final var document = handling.getDokument();
		final var archiveResponse = new ArchiveResponse();
		archiveResponse.setArchiveId("123456");

		final var archiveHistory = createRandomArchiveHistory();

		when(archiveHistoryRepositoryMock.save(archiveHistory)).thenReturn(archiveHistory);
		when(archiveIntegrationMock.archive(byggRArchiveRequestCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(archiveResponse);
		when(fastighetService.getFastighet(any())).thenReturn(new FastighetTyp());

		final var result = archiveAttachmentService.archiveAttachment(arende, handling, document, archiveHistory, MUNICIPALITY_ID);

		// Assert and verify
		assertThat(result).isNotNull();
		assertThat(result.getArchiveId()).isEqualTo(archiveResponse.getArchiveId());
		assertThat(result.getArchiveStatus()).isEqualTo(ArchiveStatus.COMPLETED);
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).containsIgnoringWhitespaces("""
																		<Arkivbildare>
				<Namn>Byggnadsnämnden</Namn>
				<VerksamhetstidFran>1974</VerksamhetstidFran>
				<VerksamhetstidTill>1992</VerksamhetstidTill>
			</Arkivbildare>""");
		assertThat(byggRArchiveRequestCaptor.getValue().getMetadata()).contains("<Klass>" + F_2_BYGGLOV + "</Klass>");
		verify(archiveHistoryRepositoryMock).save(any(ArchiveHistory.class));
		verify(archiveIntegrationMock).archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID));
		verify(fastighetService).getFastighet(any());
	}

	@ParameterizedTest
	@MethodSource("constructDocuments")
	void testBilagaNamn(final Dokument document) throws Exception {
		// Arrange
		final var arende = createArendeObject(List.of(PLFASE, FASSIT2, TOMTPLBE));
		arende.getHandelseLista().getHandelse().getFirst().getHandlingLista().getHandling().getFirst().setDokument(document);
		final var handling = arende.getHandelseLista().getHandelse().getFirst().getHandlingLista().getHandling().getFirst();
		final var archiveResponse = new ArchiveResponse();
		archiveResponse.setArchiveId("123456");

		final var archiveHistory = createRandomArchiveHistory();

		when(archiveHistoryRepositoryMock.save(archiveHistory)).thenReturn(archiveHistory);
		when(archiveIntegrationMock.archive(byggRArchiveRequestCaptor.capture(), eq(MUNICIPALITY_ID))).thenReturn(archiveResponse);
		when(fastighetService.getFastighet(any())).thenReturn(new FastighetTyp());

		archiveAttachmentService.archiveAttachment(arende, handling, document, archiveHistory, MUNICIPALITY_ID);

		assertThat(byggRArchiveRequestCaptor.getAllValues()).allSatisfy(request -> assertThat(request.getMetadata()).containsAnyOf(
			"Bilaga Namn=\"test.without.extension.docx\" Lank=\"Bilagor\\test.without.extension.docx\"",
			"Bilaga Namn=\"test.without extension 2.pdf\" Lank=\"Bilagor\\test.without extension 2.pdf\"",
			"Bilaga Namn=\"test.with   .extension.DOCX\" Lank=\"Bilagor\\test.with   .extension.DOCX\""));
		verify(archiveHistoryRepositoryMock).save(any(ArchiveHistory.class));
		verify(archiveIntegrationMock).archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID));
		verify(fastighetService).getFastighet(any());
	}

	@Test
	void archiveFails() throws Exception {
		// Arrange
		final var arende = createArendeObject(List.of(AttachmentCategory.ANS));
		final var handling = arende.getHandelseLista().getHandelse().getFirst().getHandlingLista().getHandling().getFirst();
		final var document = handling.getDokument();

		// archiveId is null
		final var archiveResponse = new ArchiveResponse();

		final var archiveHistory = createRandomArchiveHistory();

		when(archiveHistoryRepositoryMock.save(archiveHistory)).thenReturn(archiveHistory);
		when(archiveIntegrationMock.archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID))).thenReturn(archiveResponse);
		when(fastighetService.getFastighet(any())).thenReturn(new FastighetTyp());

		final var result = archiveAttachmentService.archiveAttachment(arende, handling, document, archiveHistory, MUNICIPALITY_ID);

		// Assert and verify
		assertThat(result).isNotNull();
		assertThat(result.getArchiveStatus()).isEqualTo(ArchiveStatus.NOT_COMPLETED);

		verify(archiveHistoryRepositoryMock).save(any(ArchiveHistory.class));
		verify(archiveIntegrationMock).archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID));
		verify(fastighetService).getFastighet(any());
	}

	@Test
	void archiveFailsWithClientProblem() throws Exception {
		// Arrange
		final var arende = createArendeObject(List.of(AttachmentCategory.ANS));
		final var handling = arende.getHandelseLista().getHandelse().getFirst().getHandlingLista().getHandling().getFirst();
		final var document = handling.getDokument();
		final var exceptionMessage = "File format is not allowed";
		final var problem = new ClientProblem(Status.BAD_REQUEST, exceptionMessage);

		final var archiveHistory = createRandomArchiveHistory();

		when(archiveHistoryRepositoryMock.save(archiveHistory)).thenReturn(archiveHistory);
		when(archiveIntegrationMock.archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID))).thenThrow(problem);
		when(fastighetService.getFastighet(any())).thenReturn(new FastighetTyp());

		final var result = archiveAttachmentService.archiveAttachment(arende, handling, document, archiveHistory, MUNICIPALITY_ID);

		// Assert and verify
		assertThat(result).isNotNull();
		assertThat(result.getArchiveStatus()).isEqualTo(ArchiveStatus.NOT_COMPLETED);

		verify(archiveHistoryRepositoryMock).save(any(ArchiveHistory.class));
		verify(archiveIntegrationMock).archive(any(ByggRArchiveRequest.class), eq(MUNICIPALITY_ID));
		verify(fastighetService).getFastighet(any());
		verify(messagingIntegrationMock).sendExtensionErrorEmail(archiveHistory, MUNICIPALITY_ID);
	}

	/**
	 * Util method for creating arende-objects
	 *
	 * @param  attachmentCategories - the documents that should be generated
	 * @return                      Arende
	 */
	private Arende createArendeObject(final List<AttachmentCategory> attachmentCategories) {
		final var arrayOfHandelseHandling = new ArrayOfHandelseHandling();
		attachmentCategories.forEach(category -> {
			final var dokument = new Dokument();
			dokument.setDokId(String.valueOf(randomInt(999999)));
			dokument.setNamn("Test filnamn");
			final var dokumentFil = new DokumentFil();
			dokumentFil.setFilAndelse("pdf");
			dokument.setFil(dokumentFil);
			dokument.setSkapadDatum(LocalDateTime.now().minusDays(30));
			final var handling = new HandelseHandling();
			handling.setTyp(category.name());
			handling.setDokument(dokument);

			arrayOfHandelseHandling.getHandling().add(handling);
		});

		final var handelse = new Handelse();
		handelse.setHandelsetyp(se.sundsvall.byggrarchiver.util.Constants.BYGGR_HANDELSETYP_ARKIV);
		handelse.setHandlingLista(arrayOfHandelseHandling);
		final var arrayOfHandelse = new ArrayOfHandelse();
		arrayOfHandelse.getHandelse().add(handelse);
		final var arende = new Arende();
		arende.setDnr("BYGG 2021-" + randomInt(999999));
		arende.setStatus(se.sundsvall.byggrarchiver.util.Constants.BYGGR_STATUS_AVSLUTAT);
		arende.setHandelseLista(arrayOfHandelse);
		arende.setObjektLista(createArrayOfAbstractArendeObjekt());

		return arende;
	}

	private ArrayOfAbstractArendeObjekt2 createArrayOfAbstractArendeObjekt() {
		final var fastighet = new Fastighet();
		fastighet.setFnr(123456);
		final var arendeFastighet = new ArendeFastighet();
		arendeFastighet.setFastighet(fastighet);
		arendeFastighet.setArHuvudObjekt(true);
		final var arrayOfAbstractArendeObjekt = new ArrayOfAbstractArendeObjekt2();
		arrayOfAbstractArendeObjekt.getAbstractArendeObjekt().add(arendeFastighet);
		return arrayOfAbstractArendeObjekt;
	}

}
