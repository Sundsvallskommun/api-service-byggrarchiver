package se.sundsvall.byggrarchiver.integration.messaging;

import static generated.se.sundsvall.messaging.MessageStatus.SENT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;
import static se.sundsvall.byggrarchiver.integration.messaging.MessagingMapper.toEmailRequest;
import static se.sundsvall.dept44.util.ResourceUtils.asString;

import generated.se.sundsvall.messaging.EmailRequest;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.sundsvall.byggrarchiver.configuration.EmailProperties;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;

@Component
@EnableConfigurationProperties(MessagingIntegrationProperties.class)
public class MessagingIntegration {

	static final String INTEGRATION_NAME = "messaging";

	private static final Logger LOG = LoggerFactory.getLogger(MessagingIntegration.class);

	private final Resource geoTekniskHandlingHtmlTemplate = new ClassPathResource("html-templates/geoteknisk_handling_template.html");

	private final Resource missingExtensionHtmlTemplate = new ClassPathResource("html-templates/missing_extension_template.html");

	private final Resource statusHtmlTemplate = new ClassPathResource("html-templates/status_template.html");

	private final MessagingClient client;

	private final EmailProperties emailProperties;

	public MessagingIntegration(final MessagingClient client, final EmailProperties emailProperties) {
		this.client = client;
		this.emailProperties = emailProperties;
	}

	public void sendExtensionErrorEmail(final ArchiveHistory archiveHistory, final String municipalityId) {
		var values = Map.of(
			"byggrCaseId", ofNullable(archiveHistory.getCaseId()).orElse(""),
			"documentName", ofNullable(archiveHistory.getDocumentName()).orElse(""),
			"documentType", ofNullable(archiveHistory.getDocumentType()).orElse(""));
		var htmlMessage = toBase64(replace(asString(missingExtensionHtmlTemplate), values));

		var emailRequest = toEmailRequest(emailProperties.extensionError(), "Manuell hantering krävs", htmlMessage);
		sendEmail(emailRequest, municipalityId);
	}

	public void sendStatusMail(final List<ArchiveHistory> archiveHistories, final Long batchId, final String municipalityId) {
		var counts = archiveHistories.stream()
			.collect(Collectors.partitioningBy(archiveHistory -> archiveHistory.getArchiveStatus().equals(COMPLETED), Collectors.counting()));

		var values = Map.of(
			"batchId", String.valueOf(batchId),
			"countCompleted", String.valueOf(counts.get(true)),
			"countNotCompleted", String.valueOf(counts.get(false)));
		var htmlMessage = toBase64(replace(asString(statusHtmlTemplate), values));
		var emailRequest = toEmailRequest(emailProperties.status(), "Arkiveringsstatus", htmlMessage);

		sendEmail(emailRequest, municipalityId);
	}

	public void sendEmailToLantmateriet(final String propertyDesignation, final ArchiveHistory archiveHistory, final String municipalityId) {
		var values = Map.of(
			"byggrCaseId", ofNullable(archiveHistory.getCaseId()).orElse(""),
			"propertyDesignation", ofNullable(propertyDesignation).orElse(""));
		var htmlMessage = toBase64(replace(asString(geoTekniskHandlingHtmlTemplate), values));
		var emailRequest = toEmailRequest(emailProperties.lantmateriet(), "Arkiverad geoteknisk handling", htmlMessage);

		sendEmail(emailRequest, municipalityId);
	}

	void sendEmail(final EmailRequest request, final String municipalityId) {
		LOG.info("Sending e-mail to: {} from: {}", request.getEmailAddress(), request.getSender().getAddress());

		ofNullable(client.sendEmail(municipalityId, request))
			.filter(response -> response.getMessageId() != null)
			.filter(response -> !response.getDeliveries().isEmpty())
			.filter(response -> response.getDeliveries().getFirst().getStatus() == SENT)
			.ifPresentOrElse(response -> LOG.info("E-mail sent to {} with message id {}", request.getEmailAddress(), response.getMessageId()),
				() -> LOG.error("Failed to send e-mail to {}, Payload: {}", request.getEmailAddress(), fromBase64(request.getHtmlMessage())));
	}

	String replace(final String source, final Map<String, String> values) {
		return new StringSubstitutor(values).replace(source);
	}

	private String toBase64(final String s) {
		return Base64.getEncoder().encodeToString(s.getBytes(UTF_8));
	}

	private String fromBase64(final String s) {
		return new String(Base64.getDecoder().decode(s), UTF_8);
	}
}
