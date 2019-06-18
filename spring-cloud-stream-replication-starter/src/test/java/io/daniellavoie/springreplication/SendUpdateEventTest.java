package io.daniellavoie.springreplication;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.ReplicationConfig;
import io.daniellavoie.spring.replication.ReplicationEvent;
import io.daniellavoie.spring.replication.ReplicationEvent.EventType;
import io.daniellavoie.spring.replication.exception.ReplicationMarshallingException;
import io.daniellavoie.springreplication.service.TestMessage;

public class SendUpdateEventTest extends ReplicationServiceTest {
	private TestMessage testMessage = new TestMessage(1, "This is a test.");

	@Test
	@SuppressWarnings("unchecked")
	public void testSendUpdateEvent() {
		MessageChannel messageChannel = Mockito.mock(MessageChannel.class);
		Mockito.when(messageChannel.send(Mockito.any())).then(invocation -> {
			Message<ReplicationEvent> message = ((Message<ReplicationEvent>) (invocation.getArgument(0)));

			TestMessage replicatedMessage = new ObjectMapper().findAndRegisterModules()
					.readValue(message.getPayload().getPayload(), TestMessage.class);

			Assert.assertEquals(EventType.UPDATE, message.getPayload().getEventType());
			Assert.assertEquals(testMessage.getId(), replicatedMessage.getId());
			Assert.assertEquals(testMessage.getMessage(), replicatedMessage.getMessage());

			return Boolean.TRUE;
		});

		setup(Optional.empty(), Optional.empty(), Optional.of(messageChannel), Optional.empty(), Optional.empty());

		service.save(testMessage);

		Mockito.verify(messageChannel).send(Mockito.any());
	}

	@Test
	public void testReplicationDisabled() {
		ReplicationConfig replicationConfig = new ReplicationConfig();
		replicationConfig.setEnabled(false);
		replicationConfig.setSource("default");

		setup(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(replicationConfig));

		service.save(testMessage);

		Mockito.verify(replicationEventRepository).save(Mockito.any());
		Mockito.verify(messageChannel, Mockito.never()).send(Mockito.any());
	}

	@Test(expected = ReplicationMarshallingException.class)
	public void testMarshallingErrorHandling() throws JsonProcessingException {
		ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
		Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonProcessingException.class);

		setup(Optional.of(objectMapper), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

		service.save(testMessage);
	}
}
