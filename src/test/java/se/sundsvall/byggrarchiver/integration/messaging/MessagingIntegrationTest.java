package se.sundsvall.byggrarchiver.integration.messaging;

import static generated.se.sundsvall.messaging.MessageStatus.SENT;
import static generated.se.sundsvall.messaging.MessageType.EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.byggrarchiver.api.model.enums.ArchiveStatus.COMPLETED;

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

import generated.se.sundsvall.messaging.DeliveryResult;
import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageResult;

@ExtendWith(MockitoExtension.class)
class MessagingIntegrationTest {

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
		when(mockClient.sendEmail(any(EmailRequest.class)))
			.thenReturn(new MessageResult()
				.messageId(UUID.randomUUID())
				.deliveries(List.of(
					new DeliveryResult()
						.deliveryId(UUID.randomUUID())
						.messageType(EMAIL)
						.status(SENT))));

		messagingIntegration.sendExtensionErrorEmail(new ArchiveHistory());

		verify(mockEmailProperties, times(2)).extensionError();
		verifyNoMoreInteractions(mockEmailProperties);
		verify(mockClient, times(1)).sendEmail(any(EmailRequest.class));
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void test_sendStatusEmail() {
		when(mockEmailProperties.status()).thenReturn(instance);
		when(mockClient.sendEmail(any(EmailRequest.class)))
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

		messagingIntegration.sendStatusMail(archiveHistories, 1L);

		verify(mockEmailProperties, times(2)).status();
		verifyNoMoreInteractions(mockEmailProperties);
		verify(mockClient, times(1)).sendEmail(any(EmailRequest.class));
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void test_sendEmailToLantmateriet() {
		when(mockEmailProperties.lantmateriet()).thenReturn(instance);
		when(mockClient.sendEmail(any(EmailRequest.class)))
			.thenReturn(new MessageResult()
				.messageId(UUID.randomUUID())
				.deliveries(List.of(
					new DeliveryResult()
						.deliveryId(UUID.randomUUID())
						.messageType(EMAIL)
						.status(SENT))));

		messagingIntegration.sendEmailToLantmateriet("somePropertyDesignation", new ArchiveHistory());

		verify(mockEmailProperties, times(2)).lantmateriet();
		verifyNoMoreInteractions(mockEmailProperties);
		verify(mockClient, times(1)).sendEmail(any(EmailRequest.class));
		verifyNoMoreInteractions(mockClient);
	}

	@Test
	void test_replace() {
		assertThat(messagingIntegration.replace("hello ${name}", Map.of("name", "Bob"))).isEqualTo("hello Bob");
	}

}
