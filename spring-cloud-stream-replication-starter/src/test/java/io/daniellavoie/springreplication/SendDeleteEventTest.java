package io.daniellavoie.springreplication;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.ReplicationConfig;
import io.daniellavoie.spring.replication.ReplicationEvent;
import io.daniellavoie.spring.replication.ReplicationEvent.EventType;
import io.daniellavoie.springreplication.service.TestMessage;

public class SendDeleteEventTest extends ReplicationServiceTest {
	private TestMessage testMessage = new TestMessage(1, "This is a test.");

	@Test
	@SuppressWarnings("unchecked")
	public void testSendDeleteEvent() {
		MessageChannel messageChannel = Mockito.mock(MessageChannel.class);
		Mockito.when(messageChannel.send(Mockito.any())).then(invocation -> {
			Message<ReplicationEvent> message = ((Message<ReplicationEvent>) (invocation.getArgument(0)));

			String serializedId = new ObjectMapper().findAndRegisterModules()
					.readValue(message.getPayload().getPayload(), String.class);

			Assert.assertEquals(testMessage.getId(), Long.parseLong(serializedId));
			Assert.assertEquals(EventType.DELETE, message.getPayload().getEventType());

			return Boolean.TRUE;
		});

		setup(Optional.empty(), Optional.empty(), Optional.of(messageChannel), Optional.empty(), Optional.empty());

		service.delete(testMessage.getId());

		Mockito.verify(messageChannel).send(Mockito.any());
	}

	@Test
	public void testReplicationDisabled() {
		ReplicationConfig replicationConfig = new ReplicationConfig();
		replicationConfig.setEnabled(false);
		replicationConfig.setSource("default");
		replicationConfig.setPurgeEnabled(true);
		replicationConfig.setPurgeExecutionRateMillis(60000);
		replicationConfig.setPurgeDaysToKeep(1);

		setup(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(replicationConfig));

		service.delete(testMessage.getId());

		Mockito.verify(replicationEventRepository).save(Mockito.any());
		Mockito.verify(messageChannel, Mockito.never()).send(Mockito.any());
	}
}
