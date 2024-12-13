package se.sundsvall.byggrarchiver.integration.messaging;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;
import se.sundsvall.byggrarchiver.configuration.EmailProperties;

public final class MessagingMapper {

	private static final String BYGGR_ARCHIVER_SENDER = "ByggrArchiver";

	private MessagingMapper() {
		// Prevent instantiation
	}

	static EmailRequest toEmailRequest(final EmailProperties.Instance emailProperties, final String subject, final String htmlMessage) {
		return new EmailRequest().sender(toEmailSender(emailProperties))
			.emailAddress(emailProperties.recipient())
			.subject(subject)
			.htmlMessage(htmlMessage);
	}

	static EmailSender toEmailSender(final EmailProperties.Instance emailProperties) {
		return new EmailSender()
			.name(BYGGR_ARCHIVER_SENDER)
			.address(emailProperties.sender())
			.replyTo(emailProperties.replyTo());
	}
}
