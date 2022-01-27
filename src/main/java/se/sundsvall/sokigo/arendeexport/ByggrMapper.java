package se.sundsvall.sokigo.arendeexport;

import org.jboss.logging.Logger;
import se.sundsvall.sokigo.CaseUtil;
import se.sundsvall.vo.Attachment;
import se.sundsvall.vo.AttachmentCategory;
import se.tekis.arende.*;
import se.tekis.servicecontract.ArendeBatch;
import se.tekis.servicecontract.BatchFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;

@ApplicationScoped
public class ByggrMapper {

    @Inject
    Logger log;

    @Inject
    CaseUtil caseUtil;

    public Attachment getAttachment(Handling handling, Dokument doc) {
        Attachment attachment = new Attachment();
        attachment.setCategory(getAttachmentCategory(handling.getTyp()));
        attachment.setExtension("." + doc.getFil().getFilAndelse().toLowerCase());
        attachment.setMimeType(null);
        attachment.setName(doc.getNamn());
        attachment.setNote(doc.getBeskrivning());
        attachment.setFile(caseUtil.byteArrayToBase64(doc.getFil().getFilBuffer()));

        return attachment;
    }

    public AttachmentCategory getAttachmentCategory(String handlingsTyp) {
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
    public void setLowerExclusiveBoundWithReturnedValue(BatchFilter filter, ArendeBatch arendeBatch) {
        if (arendeBatch != null) {
            log.info("Last ArendeBatch start: " + arendeBatch.getBatchStart() + " end: " + arendeBatch.getBatchEnd());
            if (arendeBatch.getBatchEnd() == null
                    || arendeBatch.getBatchEnd().isEqual(filter.getLowerExclusiveBound())
                    || arendeBatch.getBatchEnd().isBefore(filter.getLowerExclusiveBound())
                    || Duration.between(arendeBatch.getBatchStart(), arendeBatch.getBatchEnd()).toMinutes() <= 60) {

                LocalDateTime plusOneHour = filter.getLowerExclusiveBound().plusHours(1);
                filter.setLowerExclusiveBound(plusOneHour.isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : plusOneHour);

            } else {
                filter.setLowerExclusiveBound(arendeBatch.getBatchEnd().isAfter(filter.getUpperInclusiveBound()) ? filter.getUpperInclusiveBound() : arendeBatch.getBatchEnd());
            }
        }
    }

}
