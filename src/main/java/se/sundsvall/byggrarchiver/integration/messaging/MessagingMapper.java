package se.sundsvall.byggrarchiver.integration.messaging;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;

public final class MessagingMapper {

	private static final String BYGGR_ARCHIVER_SENDER = "ByggrArchiver";

	private MessagingMapper() {
		// Prevent instantiation
	}

	static EmailRequest toEmailRequest(final String sender, final String htmlMessage, final String recipient, final String subject) {
		return new EmailRequest().sender(toEmailSender(sender))
			.emailAddress(recipient)
			.subject(subject)
			.htmlMessage(htmlMessage);
	}

	static EmailSender toEmailSender(final String sender) {
		return new EmailSender()
			.name(BYGGR_ARCHIVER_SENDER)
			.address(sender);
	}

}
