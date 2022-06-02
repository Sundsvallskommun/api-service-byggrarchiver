package se.sundsvall.byggrarchiver.service;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.Attachment;
import generated.se.sundsvall.archive.ByggRArchiveRequest;
import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageStatusResponse;
import generated.se.sundsvall.messaging.Sender1;
import generated.sokigo.fb.FastighetDto;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.ra.xml.e_arkiv.fgs_erms.ObjectFactory;
import se.ra.xml.e_arkiv.fgs_erms.*;
import se.sundsvall.byggrarchiver.api.model.AttachmentCategory;
import se.sundsvall.byggrarchiver.api.model.BatchTrigger;
import se.sundsvall.byggrarchiver.api.model.Status;
import se.sundsvall.byggrarchiver.integration.db.ArchiveHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.BatchHistoryRepository;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;
import se.sundsvall.byggrarchiver.integration.db.model.BatchHistory;
import se.sundsvall.byggrarchiver.integration.sundsvall.archive.ArchiveClient;
import se.sundsvall.byggrarchiver.integration.sundsvall.messaging.MessagingClient;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.service.util.Constants;
import se.sundsvall.byggrarchiver.service.util.Util;
import se.tekis.arende.*;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.BatchFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class ByggrArchiverService {

    public static final String STANGT = "Stängt";

    @Inject
    Logger log;

    @Inject
    @RestClient
    ArchiveClient archiveClient;

    @Inject
    @RestClient
    MessagingClient messagingClient;

    @Inject
    ArchiveHistoryRepository archiveHistoryRepository;

    @Inject
    BatchHistoryRepository batchHistoryRepository;

    @Inject
    FbService fbService;

    @Inject
    Util util;

    @Inject
    ArendeExportIntegrationService arendeExportIntegrationService;

    @ConfigProperty(name = "geo-email.receiver")
    String geoEmailReceiver;

    @ConfigProperty(name = "geo-email.sender")
    String geoEmailSender;

    @ConfigProperty(name = "extension-error-email.receiver")
    String extensionErrorEmailReceiver;

    @ConfigProperty(name = "extension-error-email.sender")
    String extensionErrorEmailSender;

    @ConfigProperty(name = "long-term.archive.url")
    String longTermArchiveUrl;

    public BatchHistory runBatch(LocalDate start, LocalDate end, BatchTrigger batchTrigger) throws ApplicationException {

        log.infov("Batch with BatchTrigger: {0} was started with start: {1} and end: {2}", batchTrigger, start, end);

        if (batchTrigger.equals(BatchTrigger.SCHEDULED)) {
            BatchHistory latestBatch = getLatestCompletedBatch();

            if (latestBatch != null) {
                // If this batch end-date is not after the latest batch end date, we don't need to run it again
                if (!end.isAfter(latestBatch.getEnd())) {
                    log.info("This batch does not have a later end-date(" + end + ") than the latest batch (" + latestBatch.getEnd() + "). Cancelling this batch...");
                    return null;
                }

                // If there is a gap between the latest batch end-date and this batch start-date, we would risk to miss something.
                // Therefore - set the start-date to the latest batch end-date, plus one day.
                LocalDate dayAfterLatestBatch = latestBatch.getEnd().plusDays(1);
                if (start.isAfter(dayAfterLatestBatch)) {
                    log.info("It was a gap between the latest batch end-date and this batch start-date. Sets the start-date to: " + latestBatch.getEnd().plusDays(1));
                    start = dayAfterLatestBatch;
                }

            }
        }

        // Persist the start of this batch
        BatchHistory batchHistory = new BatchHistory(start, end, batchTrigger, Status.NOT_COMPLETED);
        batchHistoryRepository.postBatchHistory(batchHistory);

        // Do the archiving
        return archive(start, end, batchHistory);
    }

    public BatchHistory reRunBatch(Long batchHistoryId) throws ApplicationException {
        log.infov("Rerun was started with batchHistoryId: {0}", batchHistoryId);

        BatchHistory batchHistory;
        try {
            batchHistory = batchHistoryRepository.getBatchHistory(batchHistoryId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getLocalizedMessage());
        }

        if (batchHistory.getStatus().equals(Status.COMPLETED)) {
            throw new BadRequestException(Constants.IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH);
        }

        log.info("Rerun batch: " + batchHistory);

        // Do the archiving
        return archive(batchHistory.getStart(), batchHistory.getEnd(), batchHistory);

    }

    private BatchHistory archive(LocalDate searchStart, LocalDate searchEnd, BatchHistory batchHistory) throws ApplicationException {

        log.info("Batch: " + batchHistory.getId() + " was started with start-date: " + searchStart + " and end-date: " + searchEnd);

        // Used for logging only
        List<se.tekis.arende.Arende> foundCases = new ArrayList<>();
        List<se.tekis.arende.Arende> foundClosedCases = new ArrayList<>();
        List<Dokument> foundDocuments = new ArrayList<>();

        LocalDateTime start = searchStart.atStartOfDay();
        LocalDateTime end = getEnd(searchEnd);
        BatchFilter batchFilter = getBatchFilter(start, end);

        ArendeBatch arendeBatch = null;

        do {
            setLowerExclusiveBoundWithReturnedValue(batchFilter, arendeBatch);

            log.info("Run batch iteration with start-date: " + batchFilter.getLowerExclusiveBound() + " and end-date: " + batchFilter.getUpperInclusiveBound());

            // Get arenden from Byggr
            arendeBatch = arendeExportIntegrationService.getUpdatedArenden(batchFilter);

            foundCases.addAll(arendeBatch.getArenden().getArende());

            List<se.tekis.arende.Arende> closedCaseList = arendeBatch.getArenden().getArende().stream()
                    .filter(arende -> arende.getStatus().equals(Constants.BYGGR_STATUS_AVSLUTAT))
                    .collect(Collectors.toList());

            for (se.tekis.arende.Arende closedCase : closedCaseList) {
                foundClosedCases.add(closedCase);

                List<HandelseHandling> handelseHandlingList = closedCase.getHandelseLista().getHandelse().stream()
                        .filter(handelse -> Constants.BYGGR_HANDELSETYP_ARKIV.equals(handelse.getHandelsetyp()))
                        .flatMap(handelse -> handelse.getHandlingLista().getHandling().stream())
                        .filter(handelseHandling -> handelseHandling.getDokument() != null)
                        .collect(Collectors.toList());

                for (Handling handling : handelseHandlingList) {

                    String docId = handling.getDokument().getDokId();

                    ArchiveHistory oldArchiveHistory = archiveHistoryRepository.getArchiveHistory(docId, closedCase.getDnr());

                    // The new archiveHistory
                    ArchiveHistory newArchiveHistory = new ArchiveHistory();

                    if (oldArchiveHistory == null) {
                        log.infov("Document-ID: {0} in combination with Case-ID: {1} does not exist in the db. Archive it..", docId, closedCase.getDnr());

                        newArchiveHistory.setDocumentId(docId);
                        newArchiveHistory.setDocumentName(handling.getDokument().getNamn());
                        newArchiveHistory.setDocumentType(getAttachmentCategory(handling.getTyp()).getDescription());
                        newArchiveHistory.setCaseId(closedCase.getDnr());
                        newArchiveHistory.setBatchHistory(batchHistory);
                        newArchiveHistory.setStatus(Status.NOT_COMPLETED);
                        archiveHistoryRepository.postArchiveHistory(newArchiveHistory);

                    } else if (oldArchiveHistory.getStatus().equals(Status.NOT_COMPLETED)) {
                        log.infov("Document-ID: {0} in combination with Case-ID: {1} existed but has the status NOT_COMPLETED. Trying again...", docId, closedCase.getDnr());

                        newArchiveHistory = oldArchiveHistory;
                        newArchiveHistory.setBatchHistory(batchHistory);

                    } else {
                        log.infov("Document-ID: {0} in combination with Case-ID: {1} is already archived.", docId, closedCase.getDnr());
                        continue;
                    }

                    // Get documents from Byggr
                    List<Dokument> dokumentList = arendeExportIntegrationService.getDocument(docId);

                    for (Dokument doc : dokumentList) {
                        foundDocuments.add(doc);
                        log.info("Document-Count: " + foundDocuments.size() + " Case-ID: " + closedCase.getDnr() + " Document name: " + doc.getNamn() + " Handlingstyp: " + handling.getTyp() + " Handling-ID: " + handling.getHandlingId() + " Document-ID: " + doc.getDokId());

                        Attachment attachment = getAttachment(doc);

                        archiveAttachment(attachment, closedCase, handling, doc, newArchiveHistory);

                        if (newArchiveHistory.getStatus().equals(Status.COMPLETED)
                                && newArchiveHistory.getArchiveId() != null
                                && getAttachmentCategory(handling.getTyp()).equals(AttachmentCategory.GEO)) {
                            // Send email to Lantmateriet with info about the archived attachment
                            sendEmailToLantmateriet(attachment, newArchiveHistory);
                        }
                    }
                }
            }
        } while (batchFilter.getLowerExclusiveBound().isBefore(end));

        log.info("\nTotal number of cases: " + foundCases.size()
                + "\nTotal number of closed cases: " + foundClosedCases.size()
                + "\nTotal number of processed documents: " + foundDocuments.size());


        if (archiveHistoryRepository.getArchiveHistories(batchHistory.getId()).stream().noneMatch(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED))) {
            // Persist that this batch is completed
            batchHistory.setStatus(Status.COMPLETED);
            batchHistoryRepository.updateBatchHistory(batchHistory);
        }

        log.info("Batch with ID: " + batchHistory.getId() + " is " + batchHistory.getStatus());

        return batchHistory;
    }

    private void archiveAttachment(Attachment attachment, Arende arende, Handling handling, Dokument document, ArchiveHistory newArchiveHistory) throws ApplicationException {

        if (newArchiveHistory.getArchiveId() == null) {
            ByggRArchiveRequest archiveMessage = new ByggRArchiveRequest();
            archiveMessage.setAttachment(attachment);

            String metadataXml;
            try {
                JAXBContext context = JAXBContext.newInstance(LeveransobjektTyp.class);
                Marshaller marshaller = context.createMarshaller();
                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(new ObjectFactory().createLeveransobjekt(getLeveransobjektTyp(arende, handling, document)), stringWriter);
                metadataXml = stringWriter.toString();
            } catch (JAXBException e) {
                throw new ApplicationException("Something went wrong when trying to marshal LeveransobjektTyp", e);
            }

            archiveMessage.setMetadata(metadataXml);

            // Request to Archive
            ArchiveResponse archiveResponse = null;
            try {
                archiveResponse = archiveClient.postArchive(archiveMessage);
            } catch (Exception e) {
                log.error("Request to Archive failed. Continue with the rest.", e);

                if (e.getMessage().contains("extension must be valid") || e.getMessage().contains("File format")) {
                    log.info("The problem was related to the file extension. Send email with the information.");
                    sendEmailAboutExtensionError(newArchiveHistory);
                }
            }

            if (archiveResponse != null
                    && archiveResponse.getArchiveId() != null) {

                // Success! Set status to completed
                newArchiveHistory.setStatus(Status.COMPLETED);
                newArchiveHistory.setArchiveId(archiveResponse.getArchiveId());
                newArchiveHistory.setArchiveUrl(createArchiveUrl(newArchiveHistory.getCaseId(), newArchiveHistory.getDocumentId()));

                log.info("The archive-process of document with ID: " + newArchiveHistory.getDocumentId() + " succeeded!");
            } else {
                // Not successful... Set status to not completed
                newArchiveHistory.setStatus(Status.NOT_COMPLETED);
                log.info("The archive-process of document with ID: " + newArchiveHistory.getDocumentId() + " did not succeed.");
            }
        } else {
            log.info("ArchiveHistory already got a archive-ID. Set status to " + Status.COMPLETED);
            newArchiveHistory.setStatus(Status.COMPLETED);
        }

        archiveHistoryRepository.updateArchiveHistory(newArchiveHistory);
    }

    private String createArchiveUrl(String byggrCaseId, String byggrDocumentId) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("byggrCaseId", util.getStringOrEmpty(byggrCaseId));
        valuesMap.put("byggrDocumentId", util.getStringOrEmpty(byggrDocumentId));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        return longTermArchiveUrl + stringSubstitutor.replace(Constants.ARCHIVE_URL_QUERY);
    }

    private LeveransobjektTyp getLeveransobjektTyp(Arende arende, Handling handling, Dokument document) throws ApplicationException {
        LeveransobjektTyp leveransobjekt = new LeveransobjektTyp();
        leveransobjekt.setArkivbildarStruktur(getArkivbildarStruktur(arende.getAnkomstDatum()));
        leveransobjekt.setArkivobjektListaArenden(getArkivobjektListaArenden(arende, handling, document));

        leveransobjekt.setInformationsklass(null);
        leveransobjekt.setVerksamhetsbaseradArkivredovisning(null);
        leveransobjekt.setSystemInfo(null);
        leveransobjekt.setArkivobjektListaHandlingar(null);

        return leveransobjekt;
    }

    private ArkivobjektListaArendenTyp getArkivobjektListaArenden(Arende arende, Handling handling, Dokument document) throws ApplicationException {
        ArkivobjektArendeTyp arkivobjektArende = new ArkivobjektArendeTyp();

        arkivobjektArende.setArkivobjektID(arende.getDnr());
        ExtraID extraID = new ExtraID();
        extraID.setContent(arende.getDnr());
        arkivobjektArende.getExtraID().add(extraID);
        arkivobjektArende.setArendemening(arende.getBeskrivning());
        arkivobjektArende.setAvslutat(formatToIsoDateOrReturnNull(arende.getSlutDatum()));
        arkivobjektArende.setSkapad(formatToIsoDateOrReturnNull(arende.getRegistreradDatum()));
        StatusArande statusArande = new StatusArande();
        statusArande.setValue(STANGT);
        arkivobjektArende.setStatusArande(statusArande);
        arkivobjektArende.setArendeTyp(arende.getArendetyp());
        arkivobjektArende.setArkivobjektListaHandlingar(getArkivobjektListaHandlingar(handling, document));

        if (Objects.nonNull(arende.getObjektLista())) {
            arkivobjektArende.getFastighet().add(getFastighet(arende.getObjektLista().getAbstractArendeObjekt()));
        }

        if (arende.getAnkomstDatum() == null || arende.getAnkomstDatum().isAfter(LocalDate.of(2016, 12, 31))) {
            arkivobjektArende.getKlass().add(Constants.HANTERA_BYGGLOV);
        } else {
            arkivobjektArende.getKlass().add(Constants.F_2_BYGGLOV);
        }

        if (arende.getAnkomstDatum() != null) {
            arkivobjektArende.setNotering(String.valueOf(arende.getAnkomstDatum().getYear()));
        }

        arkivobjektArende.setInkommen(null);
        arkivobjektArende.setInformationsklass(null);
        arkivobjektArende.setArkiverat(null);
        arkivobjektArende.setBeskrivning(null);
        arkivobjektArende.setAtkomst(null);
        arkivobjektArende.setExpedierad(null);
        arkivobjektArende.setForvaringsenhetsReferens(null);
        arkivobjektArende.setGallring(null);
        arkivobjektArende.setMinaArendeoversikterKlassificering(null);
        arkivobjektArende.setMinaArendeoversikterStatus(null);
        arkivobjektArende.setSistaAnvandandetidpunkt(null);
        arkivobjektArende.setSystemidentifierare(null);
        arkivobjektArende.setUpprattad(null);

        ArkivobjektListaArendenTyp arkivobjektListaArendenTyp = new ArkivobjektListaArendenTyp();
        arkivobjektListaArendenTyp.getArkivobjektArende().add(arkivobjektArende);
        return arkivobjektListaArendenTyp;
    }

    private ArkivobjektListaHandlingarTyp getArkivobjektListaHandlingar(Handling handling, Dokument document) throws ApplicationException {
        ArkivobjektHandlingTyp arkivobjektHandling = new ArkivobjektHandlingTyp();
        arkivobjektHandling.setArkivobjektID(document.getDokId());
        arkivobjektHandling.setSkapad(formatToIsoDateOrReturnNull(document.getSkapadDatum()));
        arkivobjektHandling.getBilaga().add(getBilaga(document));
        if (Objects.nonNull(handling.getTyp())) {
            arkivobjektHandling.setHandlingstyp(getAttachmentCategory(handling.getTyp()).getArchiveClassification());
            arkivobjektHandling.setRubrik(getAttachmentCategory(handling.getTyp()).getDescription());
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

        ArkivobjektListaHandlingarTyp arkivobjektListaHandlingarTyp = new ArkivobjektListaHandlingarTyp();
        arkivobjektListaHandlingarTyp.getArkivobjektHandling().add(arkivobjektHandling);
        return arkivobjektListaHandlingarTyp;
    }

    private BilagaTyp getBilaga(Dokument document) throws ApplicationException {
        BilagaTyp bilaga = new BilagaTyp();

        if (document.getFil().getFilAndelse() == null) {
            document.getFil().setFilAndelse(util.getExtensionFromByteArray(document.getFil().getFilBuffer()));
        }
        bilaga.setNamn(getNameWithExtension(document.getNamn(), document.getFil().getFilAndelse()));
        bilaga.setBeskrivning(document.getBeskrivning());

        bilaga.setLank("Bilagor\\" + bilaga.getNamn());
        return bilaga;
    }

    private String getNameWithExtension(String name, String extension) {
        extension = extension.trim().toLowerCase();

        if (Pattern.compile(".*(\\.[a-zA-Z]{3,4})$").matcher(name).find()) {
            return name;
        } else {
            String extensionWithDot = extension.contains(".") ? extension : "." + extension;
            return name + extensionWithDot;
        }
    }

    private FastighetTyp getFastighet(List<AbstractArendeObjekt> abstractArendeObjektList) throws ApplicationException {
        FastighetTyp fastighet = new FastighetTyp();

        for (AbstractArendeObjekt abstractArendeObjekt : abstractArendeObjektList) {
            ArendeFastighet arendeFastighet;

            try {
                arendeFastighet = (ArendeFastighet) abstractArendeObjekt;
            } catch (ClassCastException e) {
                log.info("Could not cast AbstractArendeObjekt to ArendeFastighet");
                continue;
            }

            if (arendeFastighet != null
                    && arendeFastighet.isArHuvudObjekt()) {

                FastighetDto fastighetDto = fbService.getPropertyInfoByFnr(arendeFastighet.getFastighet().getFnr());

                if (fastighetDto != null) {
                    fastighet.setFastighetsbeteckning(fastighetDto.getKommun() + " " + fastighetDto.getBeteckning());
                    fastighet.setTrakt(fastighetDto.getTrakt());
                    fastighet.setObjektidentitet(fastighetDto.getUuid().toString());
                }
            }
        }

        return fastighet;
    }

    private ArkivbildarStrukturTyp getArkivbildarStruktur(LocalDate ankomstDatum) {
        ArkivbildarStrukturTyp arkivbildarStruktur = new ArkivbildarStrukturTyp();

        ArkivbildareTyp arkivbildareSundsvallsKommun = new ArkivbildareTyp();
        arkivbildareSundsvallsKommun.setNamn(Constants.SUNDSVALLS_KOMMUN);
        arkivbildareSundsvallsKommun.setVerksamhetstidFran("1974");

        ArkivbildareTyp arkivbildareByggOchMiljoNamnden = new ArkivbildareTyp();
        if (ankomstDatum == null || ankomstDatum.isAfter(LocalDate.of(2016, 12, 31))) {
            arkivbildareByggOchMiljoNamnden.setNamn(Constants.STADSBYGGNADSNAMNDEN);
            arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("2017");
            arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill(null);
        } else if (ankomstDatum.isAfter(LocalDate.of(1992, 12, 31))) {
            arkivbildareByggOchMiljoNamnden.setNamn(Constants.STADSBYGGNADSNAMNDEN);
            arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1993");
            arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill("2017");
        } else if (ankomstDatum.isBefore(LocalDate.of(1993, 01, 01))) {
            arkivbildareByggOchMiljoNamnden.setNamn(Constants.BYGGNADSNAMNDEN);
            arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1974");
            arkivbildareByggOchMiljoNamnden.setVerksamhetstidTill("1992");
        }
        arkivbildareSundsvallsKommun.setArkivbildare(arkivbildareByggOchMiljoNamnden);

        arkivbildarStruktur.setArkivbildare(arkivbildareSundsvallsKommun);
        return arkivbildarStruktur;
    }

    private String formatToIsoDateOrReturnNull(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    private String formatToIsoDateOrReturnNull(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    private BatchHistory getLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = batchHistoryRepository.getBatchHistories();

        // Filter completed batches
        batchHistoryList = batchHistoryList.stream()
                .filter(b -> b.getStatus().equals(Status.COMPLETED))
                .collect(Collectors.toList());

        // Sort by end-date of batch
        batchHistoryList = batchHistoryList.stream()
                .sorted(Comparator.comparing(BatchHistory::getEnd, Comparator.reverseOrder()))
                .collect(Collectors.toList());


        BatchHistory latestBatch = null;

        if (!batchHistoryList.isEmpty()) {

            // Get the latest batch
            latestBatch = batchHistoryList.get(0);
            log.info("The latest batch: " + latestBatch);
        }

        return latestBatch;
    }

    private void sendEmailAboutExtensionError(ArchiveHistory archiveHistory) {
        EmailRequest emailRequest = new EmailRequest();

        // Sender
        Sender1 sender = new Sender1();
        sender.setName("ByggrArchiver");
        sender.setEmailAddress(extensionErrorEmailSender);
        emailRequest.setSender(sender);

        emailRequest.setEmailAddress(extensionErrorEmailReceiver);
        emailRequest.setSubject("Manuell hantering krävs");

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("byggrCaseId", StringEscapeUtils.escapeHtml4(util.getStringOrEmpty(archiveHistory.getCaseId())));
        valuesMap.put("documentName", StringEscapeUtils.escapeHtml4(util.getStringOrEmpty(archiveHistory.getDocumentName())));
        valuesMap.put("documentType", StringEscapeUtils.escapeHtml4(util.getStringOrEmpty(archiveHistory.getDocumentType())));

        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        String htmlWithReplacedValues = stringSubstitutor.replace(Constants.EXTENSION_ERROR_HTML_TEMPLATE);
        emailRequest.setHtmlMessage(Base64.getEncoder().encodeToString(htmlWithReplacedValues.getBytes()));

        sendEmail(archiveHistory, emailRequest);
    }

    private void sendEmailToLantmateriet(Attachment attachment, ArchiveHistory archiveHistory) {

        EmailRequest emailRequest = new EmailRequest();

        // Email-attachment
        generated.se.sundsvall.messaging.Attachment emailAttachment = new generated.se.sundsvall.messaging.Attachment();
        emailAttachment.setName(attachment.getName());
        emailAttachment.setContent(attachment.getFile());
        emailRequest.setAttachments(List.of(emailAttachment));

        // Sender
        Sender1 sender = new Sender1();
        sender.setName("ByggrArchiver");
        sender.setEmailAddress(geoEmailSender);
        emailRequest.setSender(sender);

        emailRequest.setEmailAddress(geoEmailReceiver);
        emailRequest.setSubject("Arkiverad geoteknisk handling");

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("archiveUrl", StringEscapeUtils.escapeHtml4(util.getStringOrEmpty(archiveHistory.getArchiveUrl())));
        valuesMap.put("byggrCaseId", StringEscapeUtils.escapeHtml4(util.getStringOrEmpty(archiveHistory.getCaseId())));
        valuesMap.put("byggrDocumentName", StringEscapeUtils.escapeHtml4(util.getStringOrEmpty(attachment.getName())));

        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        String htmlWithReplacedValues = stringSubstitutor.replace(Constants.LANTMATERIET_HTML_TEMPLATE);

        emailRequest.setHtmlMessage(Base64.getEncoder().encodeToString(htmlWithReplacedValues.getBytes()));

        sendEmail(archiveHistory, emailRequest);
    }

    private void sendEmail(ArchiveHistory archiveHistory, EmailRequest emailRequest) {
        try {
            log.infov("Sending email to: {0} from: {1}", emailRequest.getEmailAddress(), emailRequest.getSender().getEmailAddress());

            MessageStatusResponse response = messagingClient.postEmail(emailRequest);

            if (response == null || response.getMessageId() == null || Boolean.FALSE.equals(response.getSent())) {
                throw new ApplicationException("Unexpected response from messagingClient.postEmail(): " + response);
            }

            log.infov("E-mail sent to {0} with MessageId: {1}",
                    emailRequest.getEmailAddress(),
                    response.getMessageId());

        } catch (Exception e) {
            // Just log the error and continue. We don't want to fail the whole batch because of this.
            log.errorv(e, "Something went wrong when trying to send e-mail to {0}. They need to be informed manually. ArchiveHistory: {1}",
                    emailRequest.getEmailAddress(),
                    archiveHistory);
        }
    }

    private LocalDateTime getEnd(LocalDate searchEnd) {

        if (searchEnd.isBefore(LocalDate.now())) {
            return searchEnd.atTime(23, 59, 59);
        }

        return LocalDateTime.now();
    }

    private BatchFilter getBatchFilter(LocalDateTime start, LocalDateTime end) {

        BatchFilter filter = new BatchFilter();
        filter.setLowerExclusiveBound(start);
        filter.setUpperInclusiveBound(end);

        return filter;
    }

    private Attachment getAttachment(Dokument document) throws ApplicationException {
        Attachment attachment = new Attachment();
        if (document.getFil().getFilAndelse() == null) {
            document.getFil().setFilAndelse(util.getExtensionFromByteArray(document.getFil().getFilBuffer()));
        }
        attachment.setExtension("." + document.getFil().getFilAndelse().toLowerCase());
        attachment.setName(getNameWithExtension(document.getNamn(), document.getFil().getFilAndelse()));
        attachment.setFile(util.byteArrayToBase64(document.getFil().getFilBuffer()));

        return attachment;
    }

    private AttachmentCategory getAttachmentCategory(String handlingsTyp) {
        try {
            return AttachmentCategory.valueOf(handlingsTyp);
        } catch (IllegalArgumentException e) {
            // All the "handlingstyper" we don't recognize, we set to AttachmentCategory.BIL,
            // which means they get the archiveClassification D,
            // which means that they are not public in the archive.
            return AttachmentCategory.BIL;
        }
    }

    /**
     * Sets setLowerExclusiveBound to the returned batchEnd if it is not equal or before the latest batch. If it is, we add 1 hour.
     * After this, we run the batch again.
     */
    private void setLowerExclusiveBoundWithReturnedValue(BatchFilter filter, ArendeBatch arendeBatch) {
        if (arendeBatch != null) {
            log.info("Last ArendeBatch start: " + arendeBatch.getBatchStart() + " end: " + arendeBatch.getBatchEnd());
            if (arendeBatch.getBatchEnd() == null
                    || arendeBatch.getBatchEnd().isEqual(filter.getLowerExclusiveBound())
                    || arendeBatch.getBatchEnd().isBefore(filter.getLowerExclusiveBound())) {

                LocalDateTime plusOneHour = filter.getLowerExclusiveBound().plusHours(1);
                filter.setLowerExclusiveBound(plusOneHour.isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : plusOneHour);

            } else {
                filter.setLowerExclusiveBound(arendeBatch.getBatchEnd().isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : arendeBatch.getBatchEnd());
            }
        }
    }
}
