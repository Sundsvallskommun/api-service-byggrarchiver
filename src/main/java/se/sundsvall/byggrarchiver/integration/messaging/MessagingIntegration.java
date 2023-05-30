package se.sundsvall.byggrarchiver.integration.messaging;

import static generated.se.sundsvall.messaging.MessageStatus.SENT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static se.sundsvall.dept44.util.ResourceUtils.asString;

import java.util.Base64;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import se.sundsvall.byggrarchiver.configuration.EmailProperties;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;

@Component
@EnableConfigurationProperties(MessagingIntegrationProperties.class)
public class MessagingIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingIntegration.class);

    static final String INTEGRATION_NAME = "messaging";

    private final Resource geoTekniskHandlingHtmlTemplate = new ClassPathResource("html-templates/geoteknisk_handling_template.html");
    private final Resource missingExtensionHtmlTemplate = new ClassPathResource("html-templates/missing_extension_template.html");

    private final MessagingClient client;
    private final EmailProperties emailProperties;

    public MessagingIntegration(final MessagingClient client, final EmailProperties emailProperties) {
        this.client = client;
        this.emailProperties = emailProperties;
    }

    public void sendExtensionErrorEmail(final ArchiveHistory archiveHistory) {
        var sender = new EmailSender();
        sender.setName("ByggrArchiver");
        sender.setAddress(emailProperties.extensionError().sender());

        var values = Map.of(
            "byggrCaseId", ofNullable(archiveHistory.getCaseId()).orElse(""),
            "documentName", ofNullable(archiveHistory.getDocumentName()).orElse(""),
            "documentType", ofNullable(archiveHistory.getDocumentType()).orElse("")
        );
        var htmlMessage = toBase64(replace(asString(missingExtensionHtmlTemplate), values));

        var emailRequest = new EmailRequest();
        emailRequest.setSender(sender);
        emailRequest.setEmailAddress(emailProperties.extensionError().recipient());
        emailRequest.setSubject("Manuell hantering krävs");
        emailRequest.setHtmlMessage(htmlMessage);

        sendEmail(emailRequest, archiveHistory);
    }

    public void sendEmailToLantmateriet(final String propertyDesignation, final ArchiveHistory archiveHistory) {
        var sender = new EmailSender();
        sender.setName("ByggrArchiver");
        sender.setAddress(emailProperties.lantmateriet().sender());

        var values = Map.of(
            "byggrCaseId", ofNullable(archiveHistory.getCaseId()).orElse(""),
            "propertyDesignation", ofNullable(propertyDesignation).orElse("")
        );
        var htmlMessage = toBase64(replace(asString(geoTekniskHandlingHtmlTemplate), values));

        var emailRequest = new EmailRequest();
        emailRequest.setSender(sender);
        emailRequest.setEmailAddress(emailProperties.lantmateriet().recipient());
        emailRequest.setSubject("Arkiverad geoteknisk handling");
        emailRequest.setHtmlMessage(htmlMessage);

        sendEmail(emailRequest, archiveHistory);
    }

    void sendEmail(final EmailRequest request, final ArchiveHistory archiveHistory) {
        try {
            LOG.info("Sending e-mail to: {} from: {}", request.getEmailAddress(), request.getSender().getAddress());

            var response = client.sendEmail(request);

            if (response != null && response.getMessageId() != null
                    && !response.getDeliveries().isEmpty()
                    && response.getDeliveries().get(0).getStatus() == SENT) {
                LOG.info("E-mail sent to {} with message id {}",
                    request.getEmailAddress(), response.getMessageId());

                return;
            }
        } catch (Exception e) {
            // Just log the error and continue. We don't want to fail the whole batch because of this.
            LOG.error("Something went wrong when trying to send e-mail to " + request.getEmailAddress() + ". They need to be informed manually. ArchiveHistory: " + archiveHistory, e);
        }
    }

    String replace(final String source, final Map<String, String> values) {
        return new StringSubstitutor(values).replace(source);
    }

    String toBase64(final String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
    }
}
