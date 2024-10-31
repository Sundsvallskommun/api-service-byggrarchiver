package se.sundsvall.byggrarchiver.service.mapper;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Optional.ofNullable;
import static se.sundsvall.byggrarchiver.util.Constants.BYGGNADSNAMNDEN;
import static se.sundsvall.byggrarchiver.util.Constants.STADSBYGGNADSNAMNDEN;
import static se.sundsvall.byggrarchiver.util.Constants.STANGT;
import static se.sundsvall.byggrarchiver.util.Constants.SUNDSVALLS_KOMMUN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import se.sundsvall.byggrarchiver.api.model.ArchiveHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.BatchHistoryResponse;
import se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus;
import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.util.Util;

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

public final class ArchiverMapper {

	private ArchiverMapper() {
		// Prevent instantiation
	}

	public static BilagaTyp toBilaga(final Dokument dokument) throws ApplicationException {
		if (dokument.getFil().getFilAndelse() == null) {
			dokument.getFil().setFilAndelse(Util.getExtensionFromByteArray(dokument.getFil().getFilBuffer()));
		}

		final var bilaga = new BilagaTyp();
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

	static boolean isAfter1992(final LocalDate ankomstDatum) {
		return ankomstDatum.isAfter(LocalDate.of(1992, 12, 31));
	}

	static boolean isAfter2016(final LocalDate ankomstDatum) {
		return ankomstDatum.isAfter(LocalDate.of(2016, 12, 31));
	}

	static boolean isBefore1993(final LocalDate ankomstDatum) {
		return ankomstDatum.isBefore(LocalDate.of(1993, 1, 1));
	}

	public static ArkivbildarStrukturTyp toArkivbildarStruktur(final LocalDate ankomstDatum) {

		final var arkivbildareByggOchMiljoNamnden = new ArkivbildareTyp();

		if (ankomstDatum == null || isAfter2016(ankomstDatum)) {
			arkivbildareByggOchMiljoNamnden.setNamn(STADSBYGGNADSNAMNDEN);
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("2017");
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill(null);
		} else if (isAfter1992(ankomstDatum)) {
			arkivbildareByggOchMiljoNamnden.setNamn(STADSBYGGNADSNAMNDEN);
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1993");
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill("2017");
		} else if (isBefore1993(ankomstDatum)) {
			arkivbildareByggOchMiljoNamnden.setNamn(BYGGNADSNAMNDEN);
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1974");
			arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill("1992");
		}

		final var arkivbildareSundsvallsKommun = new ArkivbildareTyp()
			.withNamn(SUNDSVALLS_KOMMUN)
			.withVerksamhetstidFran("1974")
			.withArkivbildare(arkivbildareByggOchMiljoNamnden);

		return new ArkivbildarStrukturTyp()
			.withArkivbildare(arkivbildareSundsvallsKommun);
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
		} catch (final IllegalArgumentException e) {
			// All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL, which
			// means they get the archiveClassification D, which means that they are not public in
			// the archive
			return AttachmentCategory.BIL;
		}
	}

	public static ArkivobjektArendeTyp toArkivobjektArendeTyp(final Arende2 arende, final Handling handling, final Dokument document) throws ApplicationException {

		return new ArkivobjektArendeTyp()
			.withArkivobjektID(arende.getDnr())
			.withExtraID(new ExtraID().withContent(arende.getDnr()))
			.withArendemening(arende.getBeskrivning())
			.withAvslutat(toIsoDate(arende.getSlutDatum()))
			.withSkapad(toIsoDate(arende.getRegistreradDatum()))
			.withStatusArande(new StatusArande().withValue(STANGT))
			.withArendeTyp(arende.getArendetyp())
			.withArkivobjektListaHandlingar(toArkivobjektListaHandlingar(handling, document));
	}

	public static ArchiveHistory toArchiveHistory(final Handling handling, final BatchHistory batchHistory, final String caseId, final AttachmentCategory attachmentCategory, final ArchiveStatus archiveStatus, final String municipalityId) {

		return ArchiveHistory.builder()
			.withDocumentId(getDocId(handling))
			.withDocumentName(getDocumentName(handling))
			.withMunicipalityId(municipalityId)
			.withDocumentType(getAttachmentCategoryDescription(attachmentCategory))
			.withCaseId(caseId)
			.withBatchHistory(batchHistory)
			.withArchiveStatus(archiveStatus)
			.build();
	}

	public static ByggRArchiveRequest toByggRArchiveRequest(final Dokument document, final String metaData) throws ApplicationException {
		return new ByggRArchiveRequest()
			.attachment(toAttachment(document))
			.metadata(metaData);
	}

	public static ArchiveHistoryResponse mapToArchiveHistoryResponse(final ArchiveHistory archiveHistory) {
		if (archiveHistory == null) {
			return null;
		}

		return ArchiveHistoryResponse.builder()
			.withDocumentId(archiveHistory.getDocumentId())
			.withCaseId(archiveHistory.getCaseId())
			.withDocumentName(archiveHistory.getDocumentName())
			.withDocumentType(archiveHistory.getDocumentType())
			.withArchiveId(archiveHistory.getArchiveId())
			.withArchiveUrl(archiveHistory.getArchiveUrl())
			.withArchiveStatus(archiveHistory.getArchiveStatus())
			.withTimestamp(archiveHistory.getTimestamp())
			.withBatchHistory(Optional.ofNullable(archiveHistory.getBatchHistory())
				.map(ArchiverMapper::mapToBatchHistoryResponse)
				.orElse(null))
			.build();
	}

	public static BatchHistoryResponse mapToBatchHistoryResponse(final BatchHistory batchHistory) {
		if (batchHistory == null) {
			return null;
		}

		return BatchHistoryResponse.builder()
			.withId(batchHistory.getId())
			.withStart(batchHistory.getStart())
			.withEnd(batchHistory.getEnd())
			.withArchiveStatus(batchHistory.getArchiveStatus())
			.withBatchTrigger(batchHistory.getBatchTrigger())
			.withTimestamp(batchHistory.getTimestamp())
			.build();
	}

	public static BatchHistory createBatchHistory(final LocalDate actualStart, final LocalDate end, final BatchTrigger batchTrigger, final ArchiveStatus archiveStatus, final String municipalityId) {
		return BatchHistory.builder()
			.withMunicipalityId(municipalityId)
			.withStart(actualStart)
			.withEnd(end)
			.withBatchTrigger(batchTrigger)
			.withArchiveStatus(archiveStatus).build();
	}

	private static String toNameWithExtension(final String name, final String extension) {
		final var trimmedExtension = extension.trim().toLowerCase();

		if (Pattern.compile("^[a-zA-Z0-9_. -]*\\.[a-zA-Z]{3,4}$").matcher(name).find()) {
			return name;
		} else {
			final var extensionWithDot = trimmedExtension.contains(".") ? trimmedExtension : "." + trimmedExtension;
			return name + extensionWithDot;
		}
	}

	private static Attachment toAttachment(final Dokument dokument) throws ApplicationException {
		if (dokument.getFil().getFilAndelse() == null) {
			dokument.getFil().setFilAndelse(Util.getExtensionFromByteArray(dokument.getFil().getFilBuffer()));
		}
		return new Attachment()
			.extension("." + dokument.getFil().getFilAndelse().toLowerCase())
			.name(toNameWithExtension(dokument.getNamn(), dokument.getFil().getFilAndelse()))
			.file(Util.byteArrayToBase64(dokument.getFil().getFilBuffer()));
	}

	private static ArkivobjektListaHandlingarTyp toArkivobjektListaHandlingar(final Handling handling,
		final Dokument document) throws ApplicationException {

		final var arkivobjektHandling = new ArkivobjektHandlingTyp()
			.withArkivobjektID(document.getDokId())
			.withSkapad(toIsoDate(document.getSkapadDatum()))
			.withBilaga(toBilaga(document));

		if (handling.getTyp() != null) {
			final var attachmentCategory = toAttachmentCategory(handling.getTyp());
			arkivobjektHandling.setHandlingstyp(attachmentCategory.getArchiveClassification());
			arkivobjektHandling.setRubrik(attachmentCategory.getDescription());
		}

		final var arkivobjektListaHandlingarTyp = new ArkivobjektListaHandlingarTyp();
		arkivobjektListaHandlingarTyp.getArkivobjektHandling().add(arkivobjektHandling);
		return arkivobjektListaHandlingarTyp;
	}

	private static String getDocId(final Handling handling) {
		return ofNullable(handling).map(Handling::getDokument).map(Dokument::getDokId).orElse(null);
	}

	private static String getDocumentName(final Handling handling) {
		return ofNullable(handling).map(Handling::getDokument).map(Dokument::getNamn).orElse(null);
	}

	private static String getAttachmentCategoryDescription(final AttachmentCategory attachmentCategory) {
		return ofNullable(attachmentCategory).isPresent() ? attachmentCategory.getDescription() : null;
	}

}
