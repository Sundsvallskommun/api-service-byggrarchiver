package se.sundsvall.byggrarchiver.service;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Optional.ofNullable;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.NOT_COMPLETED;
import static se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory.GEO;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.xml.bind.JAXBContext;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.byggrarchiver.api.model.enums.AttachmentCategory;
import se.sundsvall.byggrarchiver.api.model.enums.BatchTrigger;
import se.sundsvall.byggrarchiver.configuration.LongTermArchiveProperties;
import se.sundsvall.byggrarchiver.integration.archive.ArchiveIntegration;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;
import se.sundsvall.byggrarchiver.integration.messaging.MessagingIntegration;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.util.Util;

import feign.FeignException;
import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import generated.se.sundsvall.arendeexport.AbstractArendeObjekt;
import generated.se.sundsvall.arendeexport.Arende2;
import generated.se.sundsvall.arendeexport.ArendeBatch;
import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.BatchFilter;
import generated.se.sundsvall.arendeexport.Dokument;
import generated.se.sundsvall.arendeexport.Handling;
import generated.se.sundsvall.bygglov.ArkivbildarStrukturTyp;
import generated.se.sundsvall.bygglov.ArkivbildareTyp;
import generated.se.sundsvall.bygglov.ArkivobjektArendeTyp;
import generated.se.sundsvall.bygglov.ArkivobjektHandlingTyp;
import generated.se.sundsvall.bygglov.ArkivobjektListaArendenTyp;
import generated.se.sundsvall.bygglov.ArkivobjektListaHandlingarTyp;
import generated.se.sundsvall.bygglov.BilagaTyp;
import generated.se.sundsvall.bygglov.ExtraID;
import generated.se.sundsvall.bygglov.FastighetTyp;
import generated.se.sundsvall.bygglov.LeveransobjektTyp;
import generated.se.sundsvall.bygglov.ObjectFactory;
import generated.se.sundsvall.bygglov.StatusArande;

@Service
public class ByggrArchiverService {

    private static final Logger LOG = LoggerFactory.getLogger(ByggrArchiverService.class);

    private static final String STANGT = "Stängt";

    static final String ARCHIVE_URL_QUERY = "/Search?searchPath=AGS%20Bygglov&aipFilterOption=0&Arkivpakets-ID=MatchesPhrase(${archiveId})";
    static final String BYGGR_STATUS_AVSLUTAT = "Avslutat";
    static final String BYGGR_HANDELSETYP_ARKIV = "ARKIV";
    static final String F_2_BYGGLOV = "F2 Bygglov";
    static final String HANTERA_BYGGLOV = "3.1.4.1 Hantera Bygglov";
    static final String SUNDSVALLS_KOMMUN = "Sundsvalls kommun";
    static final String BYGGNADSNAMNDEN = "Byggnadsnämnden";
    static final String STADSBYGGNADSNAMNDEN = "Stadsbyggnadsnämnden";

    private final ArchiveIntegration archiveIntegration;
    private final MessagingIntegration messagingIntegration;
    private final ArchiveHistoryRepository archiveHistoryRepository;
    private final BatchHistoryRepository batchHistoryRepository;
    private final FbIntegration fbIntegration;
    private final ArendeExportIntegrationService arendeExportIntegrationService;
    private final LongTermArchiveProperties longTermArchiveProperties;

    public ByggrArchiverService(final ArchiveIntegration archiveIntegration,
            final MessagingIntegration messagingIntegration,
            final ArchiveHistoryRepository archiveHistoryRepository,
            final BatchHistoryRepository batchHistoryRepository,
            final FbIntegration fbIntegration,
            final ArendeExportIntegrationService arendeExportIntegrationService,
            final LongTermArchiveProperties longTermArchiveProperties) {
        this.archiveIntegration = archiveIntegration;
        this.messagingIntegration = messagingIntegration;
        this.archiveHistoryRepository = archiveHistoryRepository;
        this.batchHistoryRepository = batchHistoryRepository;
        this.fbIntegration = fbIntegration;
        this.arendeExportIntegrationService = arendeExportIntegrationService;
        this.longTermArchiveProperties = longTermArchiveProperties;
    }

    public BatchHistory runBatch(final LocalDate originalStart, final LocalDate end,
            final BatchTrigger batchTrigger) throws ApplicationException {
        LOG.info("Batch with BatchTrigger: {} was started with start: {} and end: {}", batchTrigger, originalStart, end);

        var actualStart = originalStart;

        if (batchTrigger.equals(BatchTrigger.SCHEDULED)) {
            var latestBatch = getLatestCompletedBatch();

            if (latestBatch != null) {
                // If this batch end-date is not after the latest batch end date, we don't need to run it again
                if (!end.isAfter(latestBatch.getEnd())) {
                    LOG.info("This batch does not have a later end-date({}) than the latest batch ({}). Cancelling this batch...", end, latestBatch.getEnd());
                    return null;
                }

                // If there is a gap between the latest batch end-date and this batch start-date, we would risk to miss something.
                // Therefore - set the start-date to the latest batch end-date, plus one day.
                var dayAfterLatestBatch = latestBatch.getEnd().plusDays(1);
                if (actualStart.isAfter(dayAfterLatestBatch)) {
                    LOG.info("It was a gap between the latest batch end-date and this batch start-date. Sets the start-date to: {}", latestBatch.getEnd().plusDays(1));
                    actualStart = dayAfterLatestBatch;
                }
            }
        }

        // Persist the start of this batch
        var batchHistory = BatchHistory.builder()
            .withStart(actualStart)
            .withEnd(end)
            .withBatchTrigger(batchTrigger)
            .withArchiveStatus(NOT_COMPLETED).build();
        batchHistoryRepository.save(batchHistory);

        // Do the archiving
        return archive(originalStart, end, batchHistory);
    }

    public BatchHistory reRunBatch(final Long batchHistoryId) throws ApplicationException {
        LOG.info("Rerun was started with batchHistoryId: {}", batchHistoryId);

        var batchHistory = batchHistoryRepository.findById(batchHistoryId)
            .orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND, "BatchHistory not found"));

        if (batchHistory.getArchiveStatus().equals(COMPLETED)) {
            throw Problem.valueOf(Status.BAD_REQUEST, "It's not possible to rerun a completed batch.");
        }

        LOG.info("Rerun batch: {}", batchHistory);

        // Do the archiving
        return archive(batchHistory.getStart(), batchHistory.getEnd(), batchHistory);
    }

    private BatchHistory archive(final LocalDate searchStart, final LocalDate searchEnd,
            final BatchHistory batchHistory) throws ApplicationException {
        LOG.info("Batch: {} was started with start-date: {} and end-date: {}", batchHistory.getId(), searchStart, searchEnd);

        var start = searchStart.atStartOfDay();
        var end = getEnd(searchEnd);
        var batchFilter = new BatchFilter()
            .withLowerExclusiveBound(start)
            .withUpperInclusiveBound(end);

        ArendeBatch arendeBatch = null;

        do {
            if (arendeBatch != null) {
                setLowerExclusiveBoundWithReturnedValue(batchFilter, arendeBatch);
            }

            LOG.info("Run batch iteration with start-date: {} and end-date: {}", batchFilter.getLowerExclusiveBound(), batchFilter.getUpperInclusiveBound());

            // Get arenden from Byggr
            arendeBatch = arendeExportIntegrationService.getUpdatedArenden(batchFilter);

            var closedCaseList = arendeBatch.getArenden().getArende().stream()
                .filter(arende -> arende.getStatus().equals(BYGGR_STATUS_AVSLUTAT))
                .toList();

            for (var closedCase : closedCaseList) {
                // Delete all not completed archive histories connected to this case
                archiveHistoryRepository.deleteArchiveHistoriesByCaseIdAndArchiveStatus(closedCase.getDnr(), NOT_COMPLETED);

                var handelseHandlingList = closedCase.getHandelseLista().getHandelse().stream()
                    .filter(handelse -> BYGGR_HANDELSETYP_ARKIV.equals(handelse.getHandelsetyp()))
                    .flatMap(handelse -> handelse.getHandlingLista().getHandling().stream())
                    .filter(handelseHandling -> handelseHandling.getDokument() != null).toList();

                for (var handling : handelseHandlingList) {
                    var newArchiveHistory = new ArchiveHistory();
                    var docId = handling.getDokument().getDokId();
                    var oldArchiveHistory = archiveHistoryRepository.getArchiveHistoryByDocumentIdAndCaseId(docId, closedCase.getDnr());

                    if (oldArchiveHistory.isPresent()) {
                        LOG.info("Document-ID: {} in combination with Case-ID: {} is already archived.", docId, closedCase.getDnr());
                        continue;
                    } else {
                        LOG.info("Document-ID: {} in combination with Case-ID: {} does not exist in the db. Archive it..", docId, closedCase.getDnr());
                        newArchiveHistory.setDocumentId(docId);
                        newArchiveHistory.setDocumentName(handling.getDokument().getNamn());
                        newArchiveHistory.setDocumentType(getAttachmentCategory(handling.getTyp()).getDescription());
                        newArchiveHistory.setCaseId(closedCase.getDnr());
                        newArchiveHistory.setBatchHistory(batchHistory);
                        newArchiveHistory.setArchiveStatus(NOT_COMPLETED);
                        archiveHistoryRepository.save(newArchiveHistory);
                    }

                    // Get documents from Byggr
                    var dokumentList = arendeExportIntegrationService.getDocument(docId);

                    for (var dokument : dokumentList) {
                        LOG.info("Case-ID: {} Document name: {} Handlingstyp: {} Handling-ID: {} Document-ID: {}",
                            closedCase.getDnr(), dokument.getNamn(), handling.getTyp(),
                            handling.getHandlingId(), dokument.getDokId());

                        archiveAttachment(getAttachment(dokument), closedCase, handling, dokument, newArchiveHistory);

                        if (newArchiveHistory.getArchiveStatus().equals(COMPLETED)
                                && newArchiveHistory.getArchiveId() != null
                                && getAttachmentCategory(handling.getTyp()).equals(GEO)) {
                            // Send email to Lantmateriet with info about the archived attachment
                            var arendeFastighetList = toArendeFastighetList(closedCase.getObjektLista().getAbstractArendeObjekt());

                            messagingIntegration.sendEmailToLantmateriet(
                                getFastighet(arendeFastighetList).getFastighetsbeteckning(), newArchiveHistory);
                        }
                    }
                }
            }
        } while (batchFilter.getLowerExclusiveBound().isBefore(end));

        var archiveHistoriesRelatedToBatch = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryId(batchHistory.getId());
        if (archiveHistoriesRelatedToBatch.stream().allMatch(archiveHistory -> archiveHistory.getArchiveStatus().equals(COMPLETED))) {
            // Persist that this batch is completed
            batchHistory.setArchiveStatus(COMPLETED);
            batchHistoryRepository.save(batchHistory);
        }

        LOG.info("Batch with ID: {} is {}", batchHistory.getId(), batchHistory.getArchiveStatus());
        LOG.info("Batch with ID: {} has {} archive histories", batchHistory.getId(), archiveHistoriesRelatedToBatch.size());

        updateStatusOfOldBatchHistories();

        return batchHistory;
    }

    /**
     * Update the status of NOT_COMPLETED old batch histories to COMPLETED if all archive histories are COMPLETED
     */
    private void updateStatusOfOldBatchHistories() {
        var notCompletedBatchHistories = batchHistoryRepository.findBatchHistoriesByArchiveStatus(NOT_COMPLETED);

        notCompletedBatchHistories.forEach(batchHistory -> {
            boolean allCompleted = archiveHistoryRepository.getArchiveHistoriesByBatchHistoryId(batchHistory.getId())
                .stream()
                .allMatch(archiveHistory -> archiveHistory.getArchiveStatus().equals(COMPLETED));

            if (allCompleted) {
                batchHistory.setArchiveStatus(COMPLETED);
                batchHistoryRepository.save(batchHistory);

                LOG.info("Old batch with ID: {} was NOT_COMPLETED but is now COMPLETED", batchHistory.getId());
            }
        });
    }

    private void archiveAttachment(Attachment attachment, Arende2 arende, Handling handling, Dokument document, ArchiveHistory newArchiveHistory) throws ApplicationException {
        if (newArchiveHistory.getArchiveId() == null) {
            var request = new ByggRArchiveRequest();
            request.setAttachment(attachment);

            String metadataXml;
            var leveransObjektTyp = getLeveransobjektTyp(arende, handling, document);
            try {
                var context = JAXBContext.newInstance(LeveransobjektTyp.class);
                var marshaller = context.createMarshaller();
                var stringWriter = new StringWriter();
                marshaller.marshal(new ObjectFactory().createLeveransobjekt(leveransObjektTyp), stringWriter);
                metadataXml = stringWriter.toString();
            } catch (Exception e) {
                throw new ApplicationException("Something went wrong when trying to marshal LeveransobjektTyp", e);
            }

            request.setMetadata(metadataXml);

            // Request to Archive
            ArchiveResponse archiveResponse = null;
            try {
                archiveResponse = archiveIntegration.archive(request);
            } catch (FeignException e ) {
                LOG.error("Request to Archive failed. Continue with the rest.", e);

                if (e.getMessage().contains("extension must be valid") || e.getMessage().contains("File format")) {
                    LOG.info("The problem was related to the file extension. Send email with the information.");

                    messagingIntegration.sendExtensionErrorEmail(newArchiveHistory);
                }
            }

            if (archiveResponse != null && archiveResponse.getArchiveId() != null) {
                // Success! Set status to completed
                LOG.info("The archive-process of document with ID: {} succeeded!", newArchiveHistory.getDocumentId());

                newArchiveHistory.setArchiveStatus(COMPLETED);
                newArchiveHistory.setArchiveId(archiveResponse.getArchiveId());
                newArchiveHistory.setArchiveUrl(createArchiveUrl(newArchiveHistory.getArchiveId()));
            } else {
                // Not successful... Set status to not completed
                LOG.info("The archive-process of document with ID: {} did not succeed.", newArchiveHistory.getDocumentId());

                newArchiveHistory.setArchiveStatus(NOT_COMPLETED);
            }
        } else {
            LOG.info("ArchiveHistory already got a archive-ID. Set status to {}", COMPLETED);

            newArchiveHistory.setArchiveStatus(COMPLETED);
        }

        archiveHistoryRepository.save(newArchiveHistory);
    }

    private String createArchiveUrl(final String archiveId) {
        var values = Map.of(
            "archiveId", ofNullable(archiveId).orElse("")
        );

        return longTermArchiveProperties.url() + replace(ARCHIVE_URL_QUERY, values);
    }

    private LeveransobjektTyp getLeveransobjektTyp(final Arende2 arende, final Handling handling,
            final Dokument document) throws ApplicationException {
        var leveransobjekt = new LeveransobjektTyp();
        leveransobjekt.setArkivbildarStruktur(getArkivbildarStruktur(arende.getAnkomstDatum()));
        leveransobjekt.setArkivobjektListaArenden(getArkivobjektListaArenden(arende, handling, document));
        return leveransobjekt;
    }

    private ArkivobjektListaArendenTyp getArkivobjektListaArenden(final Arende2 arende,
            final Handling handling, final Dokument document) throws ApplicationException {
        var extraId = new ExtraID();
        extraId.setContent(arende.getDnr());

        var statusArande = new StatusArande();
        statusArande.setValue(STANGT);

        var arkivobjektArende = new ArkivobjektArendeTyp();
        arkivobjektArende.setArkivobjektID(arende.getDnr());
        arkivobjektArende.getExtraID().add(extraId);
        arkivobjektArende.setArendemening(arende.getBeskrivning());
        arkivobjektArende.setAvslutat(formatToIsoDateOrReturnNull(arende.getSlutDatum()));
        arkivobjektArende.setSkapad(formatToIsoDateOrReturnNull(arende.getRegistreradDatum()));
        arkivobjektArende.setStatusArande(statusArande);
        arkivobjektArende.setArendeTyp(arende.getArendetyp());
        arkivobjektArende.setArkivobjektListaHandlingar(getArkivobjektListaHandlingar(handling, document));

        if (arende.getObjektLista() != null) {
            var arendeFastighetList = toArendeFastighetList(arende.getObjektLista().getAbstractArendeObjekt());

            arkivobjektArende.getFastighet().add(getFastighet(arendeFastighetList));
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

    private ArkivobjektListaHandlingarTyp getArkivobjektListaHandlingar(final Handling handling,
            final Dokument document) throws ApplicationException {
        var arkivobjektHandling = new ArkivobjektHandlingTyp();
        arkivobjektHandling.setArkivobjektID(document.getDokId());
        arkivobjektHandling.setSkapad(formatToIsoDateOrReturnNull(document.getSkapadDatum()));
        arkivobjektHandling.getBilaga().add(getBilaga(document));
        if (handling.getTyp() != null) {
            var attachmentCategory = getAttachmentCategory(handling.getTyp());
            arkivobjektHandling.setHandlingstyp(attachmentCategory.getArchiveClassification());
            arkivobjektHandling.setRubrik(attachmentCategory.getDescription());
        }
        arkivobjektHandling.setInformationsklass(null);
        arkivobjektHandling.setInkommen(null);
        arkivobjektHandling.setAtkomst(null);
        arkivobjektHandling.setAvsandare(null);
        arkivobjektHandling.setBeskrivning(null);
        arkivobjektHandling.setExpedierad(null);
        arkivobjektHandling.setForvaringsenhetsReferens(null);
        arkivobjektHandling.setGallring(null);
        arkivobjektHandling.setLopnummer(null);
        arkivobjektHandling.setNotering(null);
        arkivobjektHandling.setSistaAnvandandetidpunkt(null);
        arkivobjektHandling.setSkannad(null);
        arkivobjektHandling.setStatusHandling(null);
        arkivobjektHandling.setSystemidentifierare(null);
        arkivobjektHandling.setUpprattad(null);

        var arkivobjektListaHandlingarTyp = new ArkivobjektListaHandlingarTyp();
        arkivobjektListaHandlingarTyp.getArkivobjektHandling().add(arkivobjektHandling);
        return arkivobjektListaHandlingarTyp;
    }

    private BilagaTyp getBilaga(final Dokument dokument) throws ApplicationException {
        if (dokument.getFil().getFilAndelse() == null) {
            dokument.getFil().setFilAndelse(Util.getExtensionFromByteArray(dokument.getFil().getFilBuffer()));
        }

        var bilaga = new BilagaTyp();
        bilaga.setNamn(getNameWithExtension(dokument.getNamn(), dokument.getFil().getFilAndelse()));
        bilaga.setBeskrivning(dokument.getBeskrivning());
        bilaga.setLank("Bilagor\\" + bilaga.getNamn());
        return bilaga;
    }

    private String getNameWithExtension(final String name, final String extension) {
        var trimmedExtension = extension.trim().toLowerCase();

        if (Pattern.compile(".*(\\.[a-zA-Z]{3,4})$").matcher(name).find()) {
            return name;
        } else {
            var extensionWithDot = trimmedExtension.contains(".") ? trimmedExtension : "." + trimmedExtension;
            return name + extensionWithDot;
        }
    }

    private List<ArendeFastighet> toArendeFastighetList(final List<AbstractArendeObjekt> abstractArendeObjektList) {
        return abstractArendeObjektList.stream()
            .filter(ArendeFastighet.class::isInstance)
            .map(ArendeFastighet.class::cast)
            .toList();
    }

    private FastighetTyp getFastighet(final List<ArendeFastighet> arendeFastighetList) throws ApplicationException {
        var fastighet = new FastighetTyp();

        for (var arendeFastighet : arendeFastighetList) {
            if (arendeFastighet != null && arendeFastighet.isArHuvudObjekt()) {
                var fastighetDto = fbIntegration.getPropertyInfoByFnr(arendeFastighet.getFastighet().getFnr());

                if (fastighetDto != null) {
                    fastighet.setFastighetsbeteckning(fastighetDto.getKommun() + " " + fastighetDto.getBeteckning());
                    fastighet.setTrakt(fastighetDto.getTrakt());
                    fastighet.setObjektidentitet(fastighetDto.getUuid().toString());
                }
            }
        }

        return fastighet;
    }

    private ArkivbildarStrukturTyp getArkivbildarStruktur(final LocalDate ankomstDatum) {
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

    private String formatToIsoDateOrReturnNull(final LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(ISO_DATE);
    }

    private String formatToIsoDateOrReturnNull(final LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(ISO_DATE);
    }

    private BatchHistory getLatestCompletedBatch() {
        var batchHistoryList = batchHistoryRepository.findAll();

        // Filter completed batches
        batchHistoryList = batchHistoryList.stream()
            .filter(b -> b.getArchiveStatus().equals(COMPLETED))
            .toList();

        // Sort by end-date of batch
        batchHistoryList = batchHistoryList.stream()
            .sorted(Comparator.comparing(BatchHistory::getEnd, Comparator.reverseOrder()))
            .toList();

        // Get the latest batch
        return batchHistoryList.stream()
            .findFirst()
            .map(latestBatch -> {
                LOG.info("The latest batch: {}", latestBatch);

                return latestBatch;
            })
            .orElse(null);
    }

    private LocalDateTime getEnd(final LocalDate searchEnd) {
        var now = LocalDateTime.now(ZoneId.systemDefault());
        if (searchEnd.isBefore(now.toLocalDate())) {
            return searchEnd.atTime(23, 59, 59);
        }

        return LocalDateTime.now(ZoneId.systemDefault());
    }

    private Attachment getAttachment(final Dokument dokument) throws ApplicationException {
        if (dokument.getFil().getFilAndelse() == null) {
            dokument.getFil().setFilAndelse(Util.getExtensionFromByteArray(dokument.getFil().getFilBuffer()));
        }
        var attachment = new Attachment();
        attachment.setExtension("." + dokument.getFil().getFilAndelse().toLowerCase());
        attachment.setName(getNameWithExtension(dokument.getNamn(), dokument.getFil().getFilAndelse()));
        attachment.setFile(Util.byteArrayToBase64(dokument.getFil().getFilBuffer()));
        return attachment;
    }

    private AttachmentCategory getAttachmentCategory(final String handlingsTyp) {
        try {
            return AttachmentCategory.fromCode(handlingsTyp);
        } catch (IllegalArgumentException e) {
            // All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL, which
            // means they get the archiveClassification D, which means that they are not public in
            // the archive
            return AttachmentCategory.BIL;
        }
    }

    /**
     * Sets setLowerExclusiveBound to the returned batchEnd if it is not equal or before the latest batch. If it is, we add 1 hour.
     * After this, we run the batch again.
     */
    private void setLowerExclusiveBoundWithReturnedValue(final BatchFilter filter, final ArendeBatch arendeBatch) {
        LOG.info("Last ArendeBatch start: {} end: {}", arendeBatch.getBatchStart(), arendeBatch.getBatchEnd());

        if (arendeBatch.getBatchEnd() == null
                || arendeBatch.getBatchEnd().isEqual(filter.getLowerExclusiveBound())
                || arendeBatch.getBatchEnd().isBefore(filter.getLowerExclusiveBound())) {
            var plusOneHour = filter.getLowerExclusiveBound().plusHours(1);
            filter.setLowerExclusiveBound(plusOneHour.isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : plusOneHour);
        } else {
            filter.setLowerExclusiveBound(arendeBatch.getBatchEnd().isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : arendeBatch.getBatchEnd());
        }
    }

    private String replace(final String source, final Map<String, String> values) {
        return new StringSubstitutor(values).replace(source);
    }
}
