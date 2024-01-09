package se.sundsvall.byggrarchiver.integration.messaging;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.sundsvall.byggrarchiver.configuration.EmailProperties;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static generated.se.sundsvall.messaging.MessageStatus.SENT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.dept44.util.ResourceUtils.asString;

@Component
@EnableConfigurationProperties(MessagingIntegrationProperties.class)
public class MessagingIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingIntegration.class);

    static final String INTEGRATION_NAME = "messaging";
    static final String BYGGR_ARCHIVER_SENDER = "ByggrArchiver";

    private final Resource geoTekniskHandlingHtmlTemplate = new ClassPathResource("html-templates/geoteknisk_handling_template.html");
    private final Resource missingExtensionHtmlTemplate = new ClassPathResource("html-templates/missing_extension_template.html");

    private final Resource statusHtmlTemplate = new ClassPathResource("html-templates/status_template.html");

    private final MessagingClient client;
    private final EmailProperties emailProperties;

    public MessagingIntegration(final MessagingClient client, final EmailProperties emailProperties) {
        this.client = client;
        this.emailProperties = emailProperties;
    }

    public void sendExtensionErrorEmail(final ArchiveHistory archiveHistory) {
        final var sender = new EmailSender();
        sender.setName(BYGGR_ARCHIVER_SENDER);
        sender.setAddress(emailProperties.extensionError().sender());

        final var values = Map.of(
            "byggrCaseId", ofNullable(archiveHistory.getCaseId()).orElse(""),
            "documentName", ofNullable(archiveHistory.getDocumentName()).orElse(""),
            "documentType", ofNullable(archiveHistory.getDocumentType()).orElse("")
        );
        final var htmlMessage = toBase64(replace(asString(missingExtensionHtmlTemplate), values));

        final var emailRequest = new EmailRequest();
        emailRequest.setSender(sender);
        emailRequest.setEmailAddress(emailProperties.extensionError().recipient());
        emailRequest.setSubject("Manuell hantering krävs");
        emailRequest.setHtmlMessage(htmlMessage);

        sendEmail(emailRequest, archiveHistory);
    }

    public void sendStatusMail(final List<ArchiveHistory> archiveHistories, final Long batchId) {
        final var sender = new EmailSender();
        sender.setName(BYGGR_ARCHIVER_SENDER);
        sender.setAddress(emailProperties.status().sender());

        final var countTotal = archiveHistories.size();

        final var countCompleted = archiveHistories.stream()
        		.filter(archiveHistory -> archiveHistory.getArchiveStatus().equals(COMPLETED))
        		.count();
        final var countNotCompleted = countTotal - countCompleted;

        final var values = Map.of(
            "batchId", String.valueOf(batchId),
            "countCompleted", String.valueOf(countCompleted),
            "countNotCompleted", String.valueOf(countNotCompleted));
        final var htmlMessage = toBase64(replace(asString(statusHtmlTemplate), values));

        final var emailRequest = new EmailRequest();
        emailRequest.setSender(sender);
        emailRequest.setEmailAddress(emailProperties.status().recipient());
        emailRequest.setSubject("Arkiveringsstatus");
        emailRequest.setHtmlMessage(htmlMessage);

        sendEmail(emailRequest, batchId);
    }

    public void sendEmailToLantmateriet(final String propertyDesignation, final ArchiveHistory archiveHistory) {
        final var sender = new EmailSender();
        sender.setName(BYGGR_ARCHIVER_SENDER);
        sender.setAddress(emailProperties.lantmateriet().sender());

        final var values = Map.of(
            "byggrCaseId", ofNullable(archiveHistory.getCaseId()).orElse(""),
            "propertyDesignation", ofNullable(propertyDesignation).orElse("")
        );
        final var htmlMessage = toBase64(replace(asString(geoTekniskHandlingHtmlTemplate), values));

        final var emailRequest = new EmailRequest();
        emailRequest.setSender(sender);
        emailRequest.setEmailAddress(emailProperties.lantmateriet().recipient());
        emailRequest.setSubject("Arkiverad geoteknisk handling");
        emailRequest.setHtmlMessage(htmlMessage);

        sendEmail(emailRequest, archiveHistory);
    }

    void sendEmail(final EmailRequest request, final ArchiveHistory archiveHistory) {
        try {
            LOG.info("Sending e-mail to: {} from: {}", request.getEmailAddress(), request.getSender().getAddress());

            final var response = client.sendEmail(request);

            if (response != null && response.getMessageId() != null
                    && !response.getDeliveries().isEmpty()
                    && response.getDeliveries().get(0).getStatus() == SENT) {
                LOG.info("E-mail sent to {} with message id {}",
                    request.getEmailAddress(), response.getMessageId());
            }
        } catch (Exception e) {
            // Just log the error and continue. We don't want to fail the whole batch because of this.
            LOG.error("Something went wrong when trying to send e-mail to " + request.getEmailAddress() + ". They need to be informed manually. ArchiveHistory: " + archiveHistory, e);
        }
    }

    void sendEmail(final EmailRequest request, final Long batchId) {
        try {
            LOG.info("Sending e-mail to: {} from: {}", request.getEmailAddress(), request.getSender().getAddress());

            final var response = client.sendEmail(request);

            if (response != null && response.getMessageId() != null
                && !response.getDeliveries().isEmpty()
                && response.getDeliveries().get(0).getStatus() == SENT) {
                LOG.info("E-mail sent to {} with message id {}",
                    request.getEmailAddress(), response.getMessageId());
            }
        } catch (Exception e) {
            // Just log the error and continue. We don't want to fail the whole batch because of this.
            LOG.error("Something went wrong when trying to send e-mail to " + request.getEmailAddress() + ". They need to be informed manually. BatchId: " + batchId, e);
        }
    }

    String replace(final String source, final Map<String, String> values) {
        return new StringSubstitutor(values).replace(source);
    }

    String toBase64(final String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
    }
}
