package se.sundsvall.byggrarchiver.service;

import feign.FeignException;
import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.Handling;
import generated.se.sundsvall.bygglov.ArkivobjektListaArendenTyp;
import generated.se.sundsvall.bygglov.LeveransobjektTyp;
import generated.se.sundsvall.bygglov.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.configuration.LongTermArchiveProperties;
import se.sundsvall.byggrarchiver.integration.archive.ArchiveIntegration;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.service.Constants.F_2_BYGGLOV;
import static se.sundsvall.byggrarchiver.service.Constants.HANTERA_BYGGLOV;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toArendeFastighetList;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toArkivbildarStruktur;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toArkivobjektArendeTyp;
import static se.sundsvall.byggrarchiver.service.mapper.ArchiverMapper.toByggRArchiveRequest;

@Service
public class ArchiveAttachmentService {

	static final String ARCHIVE_URL_QUERY = "/Search?searchPath=AGS%20Bygglov&aipFilterOption=0&Arkivpakets-ID=MatchesPhrase(${archiveId})";
	private static final Logger LOG = LoggerFactory.getLogger(ArchiveAttachmentService.class);
	@Autowired
	LongTermArchiveProperties longTermArchiveProperties;
	@Autowired
	private ArchiveHistoryRepository archiveHistoryRepository;
	@Autowired
	private MessagingIntegration messagingIntegration;
	@Autowired
	private ArchiveIntegration archiveIntegration;
	@Autowired
	private FastighetService fastighetService;

	public ArchiveHistory archiveAttachment(final Arende2 arende, final Handling handling, final Dokument document, final ArchiveHistory archiveHistory) throws ApplicationException {

		// Request to Archive
		ArchiveResponse archiveResponse = null;
		try {
			archiveResponse = archiveIntegration.archive(toByggRArchiveRequest(document, createMetadata(arende, handling, document)));
		} catch (FeignException e) {
			LOG.error("Request to Archive failed. Continue with the rest.", e);

			if (e.getMessage().contains("extension must be valid") || e.getMessage().contains("File format")) {
				LOG.info("The problem was related to the file extension. Send email with the information.");

				messagingIntegration.sendExtensionErrorEmail(archiveHistory);
			}
		}

		if (archiveResponse != null && archiveResponse.getArchiveId() != null) {
			// Success! Set status to completed
			LOG.info("The archive-process of document with ID: {} succeeded!", archiveHistory.getDocumentId());

			archiveHistory.setArchiveStatus(COMPLETED);
			archiveHistory.setArchiveId(archiveResponse.getArchiveId());
			archiveHistory.setArchiveUrl(createArchiveUrl(archiveHistory.getArchiveId()));
		} else {
			// Not successful... Set status to not completed
			LOG.info("The archive-process of document with ID: {} did not succeed.", archiveHistory.getDocumentId());

			archiveHistory.setArchiveStatus(NOT_COMPLETED);
		}

		return archiveHistoryRepository.save(archiveHistory);
	}

	private String createArchiveUrl(final String archiveId) {
		var values = Map.of(
			"archiveId", ofNullable(archiveId).orElse("")
		);

		return longTermArchiveProperties.url() + replace(ARCHIVE_URL_QUERY, values);
	}

	private String replace(final String source, final Map<String, String> values) {
		return new StringSubstitutor(values).replace(source);
	}

	private ArkivobjektListaArendenTyp toArkivobjektListaArenden(final Arende2 arende,
		final Handling handling, final Dokument document) throws ApplicationException {

		var arkivobjektArende = toArkivobjektArendeTyp(arende, handling, document);

		if (arende.getObjektLista() != null) {
			var arendeFastighetList = toArendeFastighetList(arende.getObjektLista().getAbstractArendeObjekt());

			arkivobjektArende.getFastighet().add(fastighetService.getFastighet(arendeFastighetList));
		}

		if (arende.getAnkomstDatum() == null || arende.getAnkomstDatum().isAfter(LocalDate.of(2016, 12, 31))) {
			arkivobjektArende.getKlass().add(HANTERA_BYGGLOV);
		} else {
			arkivobjektArende.getKlass().add(F_2_BYGGLOV);
		}

		if (arende.getAnkomstDatum() != null) {
			arkivobjektArende.setNotering(String.valueOf(arende.getAnkomstDatum().getYear()));
		}

		var arkivobjektListaArendenTyp = new ArkivobjektListaArendenTyp();
		arkivobjektListaArendenTyp.getArkivobjektArende().add(arkivobjektArende);
		return arkivobjektListaArendenTyp;
	}

	private LeveransobjektTyp toLeveransobjektTyp(final Arende2 arende, final Handling handling,
		final Dokument document) throws ApplicationException {
		var leveransobjekt = new LeveransobjektTyp();
		leveransobjekt.setArkivbildarStruktur(toArkivbildarStruktur(arende.getAnkomstDatum()));
		leveransobjekt.setArkivobjektListaArenden(toArkivobjektListaArenden(arende, handling, document));
		return leveransobjekt;
	}

	private String createMetadata(final Arende2 arende, final Handling handling, final Dokument document) throws ApplicationException {
		var leveransObjektTyp = toLeveransobjektTyp(arende, handling, document);
		try {
			var context = JAXBContext.newInstance(LeveransobjektTyp.class);
			var marshaller = context.createMarshaller();
			var stringWriter = new StringWriter();
			marshaller.marshal(new ObjectFactory().createLeveransobjekt(leveransObjektTyp), stringWriter);
			return stringWriter.toString();
		} catch (Exception e) {
			throw new ApplicationException("Something went wrong when trying to marshal LeveransobjektTyp", e);
		}
	}
}
