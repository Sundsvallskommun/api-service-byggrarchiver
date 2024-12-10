package se.sundsvall.byggrarchiver.integration.messaging;

import static generated.se.sundsvall.messaging.MessageStatus.SENT;
import static generated.se.sundsvall.messaging.MessageType.EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;

import generated.se.sundsvall.messaging.DeliveryResult;
import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageResult;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.configuration.EmailProperties;
import se.sundsvall.byggrarchiver.integration.db.model.ArchiveHistory;

@ExtendWith(MockitoExtension.class)
class MessagingIntegrationTest {

	private static final String MUNICIPALITY_ID = "2281";

	private final EmailProperties.Instance instance = new EmailProperties.Instance("someSender", "someRecipient");

	@Mock
	private MessagingClient mockClient;

	@Mock
	private EmailProperties mockEmailProperties;

	@InjectMocks
	private MessagingIntegration messagingIntegration;

	@Test
	void test_sendExtensionErrorEmail() {
		when(mockEmailProperties.extensionError()).thenReturn(instance);
		when(mockClient.sendEmail(eq(MUNICIPALITY_ID), any(EmailRequest.class)))
			.thenReturn(new MessageResult()
				.messageId(UUID.randomUUID())
				.deliveries(List.of(
					new DeliveryResult()
						.deliveryId(UUID.randomUUID())
						.messageType(EMAIL)
						.status(SENT))));

		messagingIntegration.sendExtensionErrorEmail(new ArchiveHistory(), MUNICIPALITY_ID);

		verify(mockEmailProperties, times(2)).extensionError();
		verifyNoMoreInteractions(mockEmailProperties);
		verify(mockClient).sendEmail(eq(MUNICIPALITY_ID), any(EmailRequest.class));
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void test_sendStatusEmail() {
		when(mockEmailProperties.status()).thenReturn(instance);
		when(mockClient.sendEmail(eq(MUNICIPALITY_ID), any(EmailRequest.class)))
			.thenReturn(new MessageResult()
				.messageId(UUID.randomUUID())
				.deliveries(List.of(
					new DeliveryResult()
						.deliveryId(UUID.randomUUID())
						.messageType(EMAIL)
						.status(SENT))));

		final var archiveHistory = new ArchiveHistory();
		archiveHistory.setArchiveStatus(COMPLETED);
		final var archiveHistories = List.of(archiveHistory);

		messagingIntegration.sendStatusMail(archiveHistories, 1L, MUNICIPALITY_ID);

		verify(mockEmailProperties, times(2)).status();
		verifyNoMoreInteractions(mockEmailProperties);
		verify(mockClient).sendEmail(eq(MUNICIPALITY_ID), any(EmailRequest.class));
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void test_sendEmailToLantmateriet() {
		when(mockEmailProperties.lantmateriet()).thenReturn(instance);
		when(mockClient.sendEmail(eq(MUNICIPALITY_ID), any(EmailRequest.class)))
			.thenReturn(new MessageResult()
				.messageId(UUID.randomUUID())
				.deliveries(List.of(
					new DeliveryResult()
						.deliveryId(UUID.randomUUID())
						.messageType(EMAIL)
						.status(SENT))));

		messagingIntegration.sendEmailToLantmateriet("somePropertyDesignation", new ArchiveHistory(), MUNICIPALITY_ID);

		verify(mockEmailProperties, times(2)).lantmateriet();
		verifyNoMoreInteractions(mockEmailProperties);
		verify(mockClient).sendEmail(eq(MUNICIPALITY_ID), any(EmailRequest.class));
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void test_replace() {
		assertThat(messagingIntegration.replace("hello ${name}", Map.of("name", "Bob"))).isEqualTo("hello Bob");
	}

}
