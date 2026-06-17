package se.sundsvall.byggrarchiver.service;

import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.Handling;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.GEO;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.getAttachmentCategory;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toArendeFastighetList;

@Service
public class LantmaterietNotifier {

	private final FbIntegration fbIntegration;
	private final MessagingIntegration messagingIntegration;

	public LantmaterietNotifier(final FbIntegration fbIntegration, final MessagingIntegration messagingIntegration) {
		this.fbIntegration = fbIntegration;
		this.messagingIntegration = messagingIntegration;
	}

	/**
	 * Notifies Lantmäteriet about a successfully archived GEO (geoteknisk undersökning) document by emailing the
	 * property designation. No-op for documents that are not GEO or were not actually archived.
	 */
	public void notifyIfGeoDocument(final Arende2 arende, final Handling handling, final ArchiveHistory archiveHistory, final String municipalityId) throws ApplicationException {
		if (COMPLETED.equals(archiveHistory.getArchiveStatus())
			&& (archiveHistory.getArchiveId() != null)
			&& GEO.equals(getAttachmentCategory(handling.getTyp()))) {
			// Send email to Lantmateriet with info about the archived attachment
			final var arendeFastighetList = toArendeFastighetList(arende.getObjektLista().getAbstractArendeObjekt());

			messagingIntegration.sendEmailToLantmateriet(
				fbIntegration.getFastighet(arendeFastighetList).getFastighetsbeteckning(), archiveHistory, municipalityId);
		}
	}

}
