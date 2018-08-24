package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import io.daniellavoie.spring.replication.ReplicationEvent.EventType;
import io.daniellavoie.spring.replication.service.TestMessage;

public class ProcessDeleteEventTest extends ReplicationServiceTest {
	private TestMessage testMessage = new TestMessage(1, "This is a test.");

	@Test
	public void testProcessDeleteEvent() {
		setup();

		List<ReplicationService<?>> replicationServices = Arrays.asList(service);

		ReplicationEventService replicationEventService = new ReplicationEventServiceImpl(replicationEventRepository,
				Optional.of(replicationServices));

		ReplicationEvent replicationEvent = new ReplicationEvent(1, LocalDateTime.now(), TestMessage.class.getName(),
				EventType.DELETE, "default", String.valueOf(testMessage.getId()));

		replicationEventService.processEvent(replicationEvent);

		Mockito.verify(testMessageRepository).delete(testMessage.getId());
	}
}
