package se.sundsvall;

import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.ra.xml.e_arkiv.fgs_erms.ObjectFactory;
import se.ra.xml.e_arkiv.fgs_erms.*;
import se.sundsvall.exceptions.ApplicationException;
import se.sundsvall.exceptions.ExternalServiceException;
import se.sundsvall.exceptions.ServiceException;
import se.sundsvall.sokigo.CaseUtil;
import se.sundsvall.sokigo.arendeexport.ArendeExportIntegrationService;
import se.sundsvall.sokigo.arendeexport.ByggrMapper;
import se.sundsvall.sokigo.fb.vo.FastighetDto;
import se.sundsvall.sundsvall.archive.ArchiveService;
import se.sundsvall.sundsvall.archive.vo.ArchiveMessage;
import se.sundsvall.sundsvall.archive.vo.ArchiveResponse;
import se.sundsvall.sundsvall.messaging.MessagingService;
import se.sundsvall.sundsvall.messaging.vo.EmailRequest;
import se.sundsvall.sundsvall.messaging.vo.MessageStatusResponse;
import se.sundsvall.sundsvall.messaging.vo.Sender1;
import se.sundsvall.util.Constants;
import se.sundsvall.vo.*;
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
import java.util.stream.Collectors;

@ApplicationScoped
public class Archiver {

    @Inject
    Logger log;

    @Inject
    @RestClient
    ArchiveService archiveService;

    @Inject
    @RestClient
    MessagingService messagingService;

    @Inject
    ArchiveDao archiveDao;

    @Inject
    ByggrMapper byggrMapper;

    @Inject
    CaseUtil caseUtil;

    @Inject
    ArendeExportIntegrationService arendeExportIntegrationService;

    public BatchHistory runBatch(LocalDate start, LocalDate end, BatchTrigger batchTrigger) throws ApplicationException {

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
        archiveDao.postBatchHistory(batchHistory);

        // Do the archiving
        return archive(start, end, batchHistory);
    }

    public BatchHistory reRunBatch(Long batchHistoryId) throws ApplicationException {
        BatchHistory batchHistory;
        try {
            batchHistory = archiveDao.getBatchHistory(batchHistoryId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getLocalizedMessage());
        }

        if (batchHistory == null) {
            throw new NotFoundException("Can't find BatchHistory with ID: " + batchHistoryId);
        }

        if (batchHistory.getStatus().equals(Status.COMPLETED)) {
            throw new BadRequestException(Constants.IT_IS_NOT_POSSIBLE_TO_RERUN_A_COMPLETED_BATCH);
        }

        log.info("Rerun batch: " + batchHistory);

        // Do the archiving
        return archive(batchHistory.getStart(), batchHistory.getEnd(), batchHistory);

    }

    private BatchHistory archive(LocalDate searchStart, LocalDate searchEnd, BatchHistory batchHistory) throws ApplicationException {

        log.info("Runs batch: " + batchHistory.getId() + " with start-date: " + searchStart + " and end-date: " + searchEnd);

        // Used for logging only
        List<se.tekis.arende.Arende> foundCases = new ArrayList<>();
        List<se.tekis.arende.Arende> foundClosedCases = new ArrayList<>();
        List<Dokument> foundDocuments = new ArrayList<>();

        LocalDateTime start = searchStart.atStartOfDay();
        LocalDateTime end = getEnd(searchEnd);
        BatchFilter batchFilter = getBatchFilter(start, end);

        ArendeBatch arendeBatch = null;

        do {
            byggrMapper.setLowerExclusiveBoundWithReturnedValue(batchFilter, arendeBatch);

            log.info("Runs batch for start-date: " + batchFilter.getLowerExclusiveBound() + " and end-date: " + batchFilter.getUpperInclusiveBound());

            // Get arenden from Byggr
            try {
                arendeBatch = arendeExportIntegrationService.getUpdatedArenden(batchFilter);
            } catch (Exception e) {
                log.error("Request to arendeExportIntegrationService.getUpdatedArenden() failed.", e);
                throw new ExternalServiceException();
            }

            foundCases.addAll(arendeBatch.getArenden().getArende());

            List<se.tekis.arende.Arende> closedCaseList = arendeBatch.getArenden().getArende().stream()
                    .filter(arende -> arende.getStatus().equals(Constants.BYGGR_STATUS_AVSLUTAT))
                    .collect(Collectors.toList());

            for (se.tekis.arende.Arende closedCase : closedCaseList) {
                foundClosedCases.add(closedCase);

                List<HandelseHandling> handelseHandlingList = closedCase.getHandelseLista().getHandelse().stream()
                        .filter(handelse -> Constants.BYGGR_HANDELSETYP_ARKIV.equals(handelse.getHandelsetyp()))
                        .flatMap(handelse -> handelse.getHandlingLista().getHandling().stream())
                        .collect(Collectors.toList());

                for (Handling handling : handelseHandlingList) {
                    if (handling.getDokument() != null) {

                        String docId = handling.getDokument().getDokId();

                        ArchiveHistory oldArchiveHistory = archiveDao.getArchiveHistory(docId, SystemType.BYGGR);

                        // The new archiveHistory
                        ArchiveHistory newArchiveHistory = new ArchiveHistory();

                        if (oldArchiveHistory == null) {
                            log.info("The document " + docId + " does not exist in the db. Archive it..");

                            newArchiveHistory.setSystemType(SystemType.BYGGR);
                            newArchiveHistory.setDocumentId(docId);
                            newArchiveHistory.setBatchHistory(batchHistory);
                            newArchiveHistory.setStatus(Status.NOT_COMPLETED);
                            archiveDao.postArchiveHistory(newArchiveHistory);

                        } else if (oldArchiveHistory.getStatus().equals(Status.NOT_COMPLETED)) {
                            log.info("The document " + docId + " existed but has the status NOT_COMPLETED. Trying again...");

                            newArchiveHistory = oldArchiveHistory;
                            newArchiveHistory.setBatchHistory(batchHistory);

                        } else {
                            log.info("The document " + docId + " is already archived.");
                            continue;
                        }

                        // Get documents from Byggr
                        List<Dokument> dokumentList;
                        try {
                            dokumentList = arendeExportIntegrationService.getDocument(docId);
                        } catch (Exception e) {
                            log.error("Request to arendeExportIntegrationService.getDocument(" + docId + ") failed.", e);
                            throw new ExternalServiceException();
                        }

                        for (Dokument doc : dokumentList) {
                            foundDocuments.add(doc);
                            log.info("Document-Count: " + foundDocuments.size() + ". Found a document that should be archived - Case-ID: " + closedCase.getDnr() + " Document-ID: " + doc.getDokId() + " Document-name: " + doc.getNamn() + " Handling-ID: " + handling.getHandlingId() + " Handlingstyp: " + handling.getTyp());

                            Attachment attachment = byggrMapper.getAttachment(handling, doc);

                            newArchiveHistory = archiveAttachment(attachment, closedCase, handling, doc, newArchiveHistory);

                            if (newArchiveHistory.getStatus().equals(Status.COMPLETED)
                                    && newArchiveHistory.getArchiveId() != null
                                    && attachment.getCategory().equals(AttachmentCategory.GEO)) {
                                boolean success = sendEmailToLantmateriet(attachment, newArchiveHistory);

                                if (!success) {
                                    newArchiveHistory.setStatus(Status.NOT_COMPLETED);
                                    archiveDao.updateArchiveHistory(newArchiveHistory);

                                    log.info("The email to Lantmateriet failed for document with archive-ID: " + newArchiveHistory.getArchiveId() + ". Set status to " + Status.NOT_COMPLETED);
                                }
                            }
                        }
                    }
                }
            }
        } while (batchFilter.getLowerExclusiveBound().isBefore(end));

        log.info("\nTotal number of cases: " + foundCases.size()
                + "\nTotal number of closed cases: " + foundClosedCases.size()
                + "\nTotal number of documents ready for archiving: " + foundDocuments.size());


        if (archiveDao.getArchiveHistories(batchHistory.getId()).stream().noneMatch(archiveHistory -> archiveHistory.getStatus().equals(Status.NOT_COMPLETED))) {
            // Persist that this batch is completed
            batchHistory.setStatus(Status.COMPLETED);
            archiveDao.updateBatchHistory(batchHistory);
        }

        log.info("Batch with ID: " + batchHistory.getId() + " is " + batchHistory.getStatus());

        return batchHistory;
    }

    private ArchiveHistory archiveAttachment(Attachment attachment, Arende arende, Handling handling, Dokument document, ArchiveHistory newArchiveHistory) throws ApplicationException {

        if (newArchiveHistory.getArchiveId() == null) {
            ArchiveMessage archiveMessage = new ArchiveMessage();
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
            ArchiveResponse archiveResponse = postArchive(archiveMessage);

            if (archiveResponse != null
                    && archiveResponse.getArchiveId() != null) {

                // Success! Set status to completed
                newArchiveHistory.setStatus(Status.COMPLETED);
                newArchiveHistory.setArchiveId(archiveResponse.getArchiveId());

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

        archiveDao.updateArchiveHistory(newArchiveHistory);

        return newArchiveHistory;
    }

    private LeveransobjektTyp getLeveransobjektTyp(Arende arende, Handling handling, Dokument document) throws ApplicationException {
        LeveransobjektTyp leveransobjekt = new LeveransobjektTyp();
        leveransobjekt.setArkivbildarStruktur(getArkivbildarStruktur());
        leveransobjekt.setArkivobjektListaArenden(getArkivobjektListaArenden(arende, handling, document));

        // TODO - I don't know what to set these fields to right now
        leveransobjekt.setInformationsklass(null);
        leveransobjekt.setVerksamhetsbaseradArkivredovisning(null);
        leveransobjekt.setSystemInfo(null);
        leveransobjekt.setArkivobjektListaHandlingar(null);

        return leveransobjekt;
    }

    private ArkivobjektListaArendenTyp getArkivobjektListaArenden(Arende arende, Handling handling, Dokument document) throws ApplicationException {
        ArkivobjektArendeTyp arkivobjektArende = new ArkivobjektArendeTyp();

        arkivobjektArende.setArkivobjektID(arende.getDnr());
        arkivobjektArende.setArendemening(arende.getBeskrivning());
        arkivobjektArende.setAvslutat(formatToIsoDateOrReturnNull(arende.getSlutDatum()));
        arkivobjektArende.setSkapad(formatToIsoDateOrReturnNull(arende.getRegistreradDatum()));
        StatusArande statusArande = new StatusArande();
        statusArande.setValue("Stängt");
        arkivobjektArende.setStatusArande(statusArande);
        arkivobjektArende.setArendeTyp(arende.getArendetyp());
        arkivobjektArende.setArkivobjektListaHandlingar(getArkivobjektListaHandlingar(handling, document));

        if (Objects.nonNull(arende.getObjektLista())) {
            arkivobjektArende.getFastighet().add(getFastighet(arende.getObjektLista().getAbstractArendeObjekt()));
        }

        // TODO - Not sure of this one...
        arkivobjektArende.getKlass().add("F2 Bygglov");

        // TODO - I don't know what to set these fields to right now
        arkivobjektArende.setInkommen(null);
        arkivobjektArende.setInformationsklass(null);
        arkivobjektArende.setArkiverat(null);
        arkivobjektArende.setBeskrivning(null);
        arkivobjektArende.setArkiverat(null);
        arkivobjektArende.setAtkomst(null);
        arkivobjektArende.setExpedierad(null);
        arkivobjektArende.setForvaringsenhetsReferens(null);
        arkivobjektArende.setGallring(null);
        arkivobjektArende.setMinaArendeoversikterKlassificering(null);
        arkivobjektArende.setMinaArendeoversikterStatus(null);
        arkivobjektArende.setNotering(null);
        arkivobjektArende.setSistaAnvandandetidpunkt(null);
        arkivobjektArende.setSystemidentifierare(null);
        arkivobjektArende.setUpprattad(null);

        ArkivobjektListaArendenTyp arkivobjektListaArendenTyp = new ArkivobjektListaArendenTyp();
        arkivobjektListaArendenTyp.getArkivobjektArende().add(arkivobjektArende);
        return arkivobjektListaArendenTyp;
    }

    private ArkivobjektListaHandlingarTyp getArkivobjektListaHandlingar(Handling handling, Dokument document) {
        ArkivobjektHandlingTyp arkivobjektHandling = new ArkivobjektHandlingTyp();
        arkivobjektHandling.setArkivobjektID(document.getDokId());
        arkivobjektHandling.setSkapad(formatToIsoDateOrReturnNull(document.getSkapadDatum()));
        arkivobjektHandling.getBilaga().add(getBilaga(document));
        if (Objects.nonNull(handling.getTyp())) {
            arkivobjektHandling.setHandlingstyp(byggrMapper.getAttachmentCategory(handling.getTyp()).getArchiveClassification());
            arkivobjektHandling.setRubrik(byggrMapper.getAttachmentCategory(handling.getTyp()).getDescription());
        }

        // TODO - I don't know what to set this fields to right now
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

    private BilagaTyp getBilaga(Dokument document) {
        BilagaTyp bilaga = new BilagaTyp();
        bilaga.setNamn(document.getNamn());
        bilaga.setBeskrivning(document.getBeskrivning());
//        bilaga.setChecksumma(document.getChecksum());

        bilaga.setLank("Bilagor\\" + bilaga.getNamn());
        return bilaga;
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

                FastighetDto fastighetDto = caseUtil.getPropertyInfoByFnr(arendeFastighet.getFastighet().getFnr());

                if (fastighetDto != null) {
                    fastighet.setFastighetsbeteckning(fastighetDto.getKommun() + " " + fastighetDto.getBeteckning());
                    fastighet.setTrakt(fastighetDto.getTrakt());
                    fastighet.setObjektidentitet(fastighetDto.getUuid());
                }
            }
        }

        return fastighet;
    }

    private ArkivbildarStrukturTyp getArkivbildarStruktur() {
        ArkivbildarStrukturTyp arkivbildarStruktur = new ArkivbildarStrukturTyp();

        ArkivbildareTyp arkivbildareSundsvallsKommun = new ArkivbildareTyp();
        arkivbildareSundsvallsKommun.setNamn("Sundsvalls kommun");
        arkivbildareSundsvallsKommun.setVerksamhetstidFran("1974");

        ArkivbildareTyp arkivbildareByggOchMiljoNamnden = new ArkivbildareTyp();
        arkivbildareByggOchMiljoNamnden.setNamn("Bygg- och miljönämnden");
        arkivbildareByggOchMiljoNamnden.setVerksamhetstidFran("1974");
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

    private ArchiveResponse postArchive(ArchiveMessage archiveMessage) {
        ArchiveResponse archiveResponse = null;
        // POST to archive
        try {
            archiveResponse = archiveService.postArchive(archiveMessage);
            log.info("Response from archive: " + archiveResponse);
        } catch (ServiceException e) {
            // Just log the error and continue with the rest
            log.error("Unexpected response from Archive when using method archiveService.postArchive -->\nHTTP Status: " + e.getStatus().getStatusCode() + " " + e.getStatus().getReasonPhrase() + "\nResponse body: " + e.getMessage());
        }
        return archiveResponse;
    }


    private BatchHistory getLatestCompletedBatch() {
        List<BatchHistory> batchHistoryList = archiveDao.getBatchHistories();

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

    /**
     * @param attachment
     * @param archiveHistory
     * @return true if successful, false if unsuccessful
     */
    private boolean sendEmailToLantmateriet(Attachment attachment, ArchiveHistory archiveHistory) {

        EmailRequest emailRequest = new EmailRequest();

        // Email-attachment
        se.sundsvall.sundsvall.messaging.vo.Attachment emailAttachment = new se.sundsvall.sundsvall.messaging.vo.Attachment();
        emailAttachment.setName(attachment.getName());
        emailAttachment.setContent(attachment.getFile());
        emailAttachment.setContentType(attachment.getMimeType());
        emailRequest.setAttachments(List.of(emailAttachment));

        // Sender
        Sender1 sender1 = new Sender1();
        sender1.setEmailAddress("dennis.nilsson@sundsvall.se");
        sender1.setName("Archiver");
        emailRequest.setSender(sender1);

        emailRequest.setEmailAddress("dennis.nilsson@b3.se");
        emailRequest.setSubject("Arkiverad geoteknisk handling");

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("archiveId", getStringOrEmpty(archiveHistory.getArchiveId()));
        valuesMap.put("archiveUrl", getStringOrEmpty(archiveHistory.getArchiveUrl()));
        valuesMap.put("byggrDocumentName", getStringOrEmpty(attachment.getName()));
        valuesMap.put("byggrDocumentId", getStringOrEmpty(archiveHistory.getDocumentId()));

        StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
        String htmlWithReplacedValues = stringSubstitutor.replace(Constants.LANTMATERIET_HTML_TEMPLATE);

        log.info("HTML:\n" + htmlWithReplacedValues);

        emailRequest.setHtmlMessage(Base64.getEncoder().encodeToString(htmlWithReplacedValues.getBytes()));

        MessageStatusResponse response = null;
        try {
            response = messagingService.postEmail(emailRequest);

            if (response != null
                    && response.isSent()) {
                log.info("E-mail sent to Lantmäteriet with information about geoteknisk handling for ArchiveId: " + archiveHistory.getArchiveId() + " MessageId: " + response.getMessageId());
                return true;
            } else {
                log.error("Something went wrong when trying to send e-mail about geoteknisk handling to Lantmäteriet." +
                        "\nArchiveId: " + archiveHistory.getArchiveId() + "" +
                        "\nDocumentId in Byggr: " + archiveHistory.getDocumentId() + "" +
                        "\nResponse from messaging: " + response);
            }

        } catch (ServiceException e) {
            log.error("The request to messagingService.postEmail failed. ServiceException was thrown.", e);
        }

        return false;
    }

    private String getStringOrEmpty(String string) {
        return string != null ? string : "";
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
}
