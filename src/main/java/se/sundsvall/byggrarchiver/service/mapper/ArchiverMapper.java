package se.sundsvall.byggrarchiver.service.mapper;

import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import generated.se.sundsvall.arendeexport.AbstractArendeObjekt;
import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.Handling;
import generated.se.sundsvall.bygglov.ArkivbildarStrukturTyp;
import generated.se.sundsvall.bygglov.ArkivbildareTyp;
import generated.se.sundsvall.bygglov.ArkivobjektArendeTyp;
import generated.se.sundsvall.bygglov.ArkivobjektHandlingTyp;
import generated.se.sundsvall.bygglov.ArkivobjektListaHandlingarTyp;
import generated.se.sundsvall.bygglov.BilagaTyp;
import generated.se.sundsvall.bygglov.ExtraID;
import generated.se.sundsvall.bygglov.StatusArande;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.util.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Optional.ofNullable;
import static se.sundsvall.byggrarchiver.service.Constants.BYGGNADSNAMNDEN;
import static se.sundsvall.byggrarchiver.service.Constants.STADSBYGGNADSNAMNDEN;
import static se.sundsvall.byggrarchiver.service.Constants.STANGT;
import static se.sundsvall.byggrarchiver.service.Constants.SUNDSVALLS_KOMMUN;

public class ArchiverMapper {

	private ArchiverMapper() {}

	public static BilagaTyp toBilaga(final Dokument dokument) throws ApplicationException {
		if (dokument.getFil().getFilAndelse() == null) {
			dokument.getFil().setFilAndelse(Util.getExtensionFromByteArray(dokument.getFil().getFilBuffer()));
		}

		var bilaga = new BilagaTyp();
		bilaga.setNamn(toNameWithExtension(dokument.getNamn(), dokument.getFil().getFilAndelse()));
		bilaga.setBeskrivning(dokument.getBeskrivning());
		bilaga.setLank("Bilagor\\" + bilaga.getNamn());
		return bilaga;
	}

	public static List<ArendeFastighet> toArendeFastighetList(final List<AbstractArendeObjekt> abstractArendeObjektList) {
		return abstractArendeObjektList.stream()
			.filter(ArendeFastighet.class::isInstance)
			.map(ArendeFastighet.class::cast)
			.toList();
	}

	public static ArkivbildarStrukturTyp toArkivbildarStruktur(final LocalDate ankomstDatum) {
		var arkivbildareByggOchMiljoNamnden = new ArkivbildareTyp();
		if (ankomstDatum == null || ankomstDatum.isAfter(LocalDate.of(2016, 12, 31))) {
			arkivbildareByggOchMiljoNamnden.setNamn(STADSBYGGNADSNAMNDEN);
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("2017");
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill(null);
		} else if (ankomstDatum.isAfter(LocalDate.of(1992, 12, 31))) {
			arkivbildareByggOchMiljoNamnden.setNamn(STADSBYGGNADSNAMNDEN);
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1993");
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill("2017");
		} else if (ankomstDatum.isBefore(LocalDate.of(1993, 1, 1))) {
			arkivbildareByggOchMiljoNamnden.setNamn(BYGGNADSNAMNDEN);
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1974");
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill("1992");
		}

		var arkivbildareSundsvallsKommun = new ArkivbildareTyp();
		arkivbildareSundsvallsKommun.setNamn(SUNDSVALLS_KOMMUN);
		arkivbildareSundsvallsKommun.setVerksamhetstidFran("1974");
		arkivbildareSundsvallsKommun.setArkivbildare(arkivbildareByggOchMiljoNamnden);

		var arkivbildarStruktur = new ArkivbildarStrukturTyp();
		arkivbildarStruktur.setArkivbildare(arkivbildareSundsvallsKommun);
		return arkivbildarStruktur;
	}

	public static String toIsoDate(final LocalDate date) {
		if (date == null) {
			return null;
		}
		return date.format(ISO_DATE);
	}

	public static String toIsoDate(final LocalDateTime date) {
		if (date == null) {
			return null;
		}
		return date.format(ISO_DATE);
	}

	public static AttachmentCategory toAttachmentCategory(final String handlingsTyp) {
		try {
			return AttachmentCategory.fromCode(handlingsTyp);
		} catch (IllegalArgumentException e) {
			// All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL, which
			// means they get the archiveClassification D, which means that they are not public in
			// the archive
			return AttachmentCategory.BIL;
		}
	}

	public static ArkivobjektArendeTyp toArkivobjektArendeTyp(final Arende2 arende, final Handling handling, final Dokument document) throws ApplicationException {
		var extraId = new ExtraID();
		extraId.setContent(arende.getDnr());

		var statusArande = new StatusArande();
		statusArande.setValue(STANGT);

		var arkivobjektArende =  new ArkivobjektArendeTyp();
		arkivobjektArende.setArkivobjektID(arende.getDnr());
		arkivobjektArende.getExtraID().add(extraId);
		arkivobjektArende.setArendemening(arende.getBeskrivning());
		arkivobjektArende.setAvslutat(toIsoDate(arende.getSlutDatum()));
		arkivobjektArende.setSkapad(toIsoDate(arende.getRegistreradDatum()));
		arkivobjektArende.setStatusArande(statusArande);
		arkivobjektArende.setArendeTyp(arende.getArendetyp());
		arkivobjektArende.setArkivobjektListaHandlingar(toArkivobjektListaHandlingar(handling, document));

		return arkivobjektArende;
	}

	public static ArchiveHistory toArchiveHistory(final Handling handling, final BatchHistory batchHistory, final String caseId, final AttachmentCategory attachmentCategory, ArchiveStatus archiveStatus) {

		var archiveHistory = new ArchiveHistory();
		archiveHistory.setDocumentId(getDocId(handling));
		archiveHistory.setDocumentName(getDocumentName(handling));
		archiveHistory.setDocumentType(getAttachmentCategoryDescription(attachmentCategory));
		archiveHistory.setCaseId(caseId);
		archiveHistory.setBatchHistory(batchHistory);
		archiveHistory.setArchiveStatus(archiveStatus);

		return archiveHistory;
	}

	public static ByggRArchiveRequest toByggRArchiveRequest(final Dokument document, String metaData) throws ApplicationException {
		var request = new ByggRArchiveRequest();
		request.setAttachment(toAttachment(document));
		request.setMetadata(metaData);

		return request;
	}

	private static String toNameWithExtension(final String name, final String extension) {
		var trimmedExtension = extension.trim().toLowerCase();

		if (Pattern.compile("^[a-zA-Z0-9_. -]*\\.[a-zA-Z]{3,4}$").matcher(name).find()) {
			return name;
		} else {
			var extensionWithDot = trimmedExtension.contains(".") ? trimmedExtension : "." + trimmedExtension;
			return name + extensionWithDot;
		}
	}

	private static Attachment toAttachment(final Dokument dokument) throws ApplicationException {
		if (dokument.getFil().getFilAndelse() == null) {
			dokument.getFil().setFilAndelse(Util.getExtensionFromByteArray(dokument.getFil().getFilBuffer()));
		}
		var attachment = new Attachment();
		attachment.setExtension("." + dokument.getFil().getFilAndelse().toLowerCase());
		attachment.setName(toNameWithExtension(dokument.getNamn(), dokument.getFil().getFilAndelse()));
		attachment.setFile(Util.byteArrayToBase64(dokument.getFil().getFilBuffer()));
		return attachment;
	}

	private static ArkivobjektListaHandlingarTyp toArkivobjektListaHandlingar(final Handling handling,
		final Dokument document) throws ApplicationException {
		var arkivobjektHandling = new ArkivobjektHandlingTyp();
		arkivobjektHandling.setArkivobjektID(document.getDokId());
		arkivobjektHandling.setSkapad(toIsoDate(document.getSkapadDatum()));
		arkivobjektHandling.getBilaga().add(toBilaga(document));
		if (handling.getTyp() != null) {
			var attachmentCategory = toAttachmentCategory(handling.getTyp());
			arkivobjektHandling.setHandlingstyp(attachmentCategory.getArchiveClassification());
			arkivobjektHandling.setRubrik(attachmentCategory.getDescription());
		}

		var arkivobjektListaHandlingarTyp = new ArkivobjektListaHandlingarTyp();
		arkivobjektListaHandlingarTyp.getArkivobjektHandling().add(arkivobjektHandling);
		return arkivobjektListaHandlingarTyp;
	}

	private static String getDocId(final Handling handling) {
		return ofNullable(handling).map(Handling::getDokument).map(Dokument::getDokId).orElse(null);
	}

	private static String getDocumentName(final Handling handling) {
		return ofNullable(handling).map(Handling::getDokument).map(Dokument::getNamn).orElse(null);
	}

	private static String getAttachmentCategoryDescription(AttachmentCategory attachmentCategory) {
		return ofNullable(attachmentCategory).isPresent() ? attachmentCategory.getDescription() : null;
	}
}
