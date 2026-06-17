package se.sundsvall.byggrarchiver.service;

import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.ArrayOfAbstractArendeObjekt2;
import generated.se.sundsvall.arendeexport.Fastighet;
import generated.se.sundsvall.arendeexport.Handling;
import generated.se.sundsvall.bygglov.FastighetTyp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.FASSIT2;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.GEO;

@ExtendWith(MockitoExtension.class)
class LantmaterietNotifierTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private FbIntegration fbIntegrationMock;

	@Mock
	private MessagingIntegration messagingIntegrationMock;

	@InjectMocks
	private LantmaterietNotifier lantmaterietNotifier;

	private static Arende2 arendeWithHuvudObjekt() {
		final var fastighet = new Fastighet();
		fastighet.setFnr(123456);
		final var arendeFastighet = new ArendeFastighet();
		arendeFastighet.setFastighet(fastighet);
		arendeFastighet.setArHuvudObjekt(true);
		final var objektLista = new ArrayOfAbstractArendeObjekt2();
		objektLista.getAbstractArendeObjekt().add(arendeFastighet);
		final var arende = new Arende2();
		arende.setObjektLista(objektLista);
		return arende;
	}

	private static Handling handling(final String typ) {
		final var handling = new Handling();
		handling.setTyp(typ);
		return handling;
	}

	private static ArchiveHistory archiveHistory(final ArchiveStatus status, final String archiveId) {
		final var archiveHistory = new ArchiveHistory();
		archiveHistory.setArchiveStatus(status);
		archiveHistory.setArchiveId(archiveId);
		return archiveHistory;
	}

	@Test
	void sendsEmailForCompletedGeoDocument() throws ApplicationException {
		final var archiveHistory = archiveHistory(COMPLETED, "archive-1");
		final var fastighet = new FastighetTyp();
		fastighet.setFastighetsbeteckning("Sundsvall Test 1");
		when(fbIntegrationMock.getFastighet(any())).thenReturn(fastighet);

		lantmaterietNotifier.notifyIfGeoDocument(arendeWithHuvudObjekt(), handling(GEO.getCode()), archiveHistory, MUNICIPALITY_ID);

		verify(messagingIntegrationMock).sendEmailToLantmateriet("Sundsvall Test 1", archiveHistory, MUNICIPALITY_ID);
	}

	@Test
	void doesNothingForNonGeoDocument() throws ApplicationException {
		lantmaterietNotifier.notifyIfGeoDocument(arendeWithHuvudObjekt(), handling(FASSIT2.getCode()), archiveHistory(COMPLETED, "archive-1"), MUNICIPALITY_ID);

		verifyNoInteractions(fbIntegrationMock, messagingIntegrationMock);
	}

	@Test
	void doesNothingWhenNotCompleted() throws ApplicationException {
		lantmaterietNotifier.notifyIfGeoDocument(arendeWithHuvudObjekt(), handling(GEO.getCode()), archiveHistory(NOT_COMPLETED, "archive-1"), MUNICIPALITY_ID);

		verifyNoInteractions(fbIntegrationMock, messagingIntegrationMock);
	}

	@Test
	void doesNothingWhenArchiveIdMissing() throws ApplicationException {
		lantmaterietNotifier.notifyIfGeoDocument(arendeWithHuvudObjekt(), handling(GEO.getCode()), archiveHistory(COMPLETED, null), MUNICIPALITY_ID);

		verifyNoInteractions(fbIntegrationMock, messagingIntegrationMock);
	}

}
