package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.ReplicationEvent.EventType;
import io.daniellavoie.spring.replication.exception.ReplicationUnmarshallingException;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;
import io.daniellavoie.spring.replication.service.TestMessage;
import io.daniellavoie.spring.replication.service.TestMessageReplicationService;
import io.daniellavoie.spring.replication.service.TestMessageRepository;

public class ProcessUpdateEventTest extends ReplicationServiceTest {
	private TestMessage testMessage = new TestMessage(1, "This is a test.");

	@Test
	public void testProcessUpdateEvent() throws JsonProcessingException {
		TestMessageRepository testMessageRepository = Mockito.mock(TestMessageRepository.class);
		Mockito.when(testMessageRepository.save(Mockito.any())).then(invocation -> {
			TestMessage testMessage = invocation.getArgument(0);

			Assert.assertEquals(this.testMessage.getId(), testMessage.getId());
			Assert.assertEquals(this.testMessage.getMessage(), testMessage.getMessage());

			return testMessage;
		});

		ReplicationEventRepository replicationEventRepository = Mockito.mock(ReplicationEventRepository.class);
		Mockito.when(replicationEventRepository.save(Mockito.any())).then(invocation -> {
			ReplicationEvent replicationEvent = invocation.getArgument(0);

			Assert.assertEquals(TestMessage.class.getName(), replicationEvent.getObjectClass());
			Assert.assertEquals(replicationConfig.getSource(), replicationEvent.getSource());
			Assert.assertNotNull(replicationEvent.getTimestamp());

			return replicationEvent;
		});

		setup(Optional.empty(), Optional.of(testMessageRepository), Optional.empty(),
				Optional.of(replicationEventRepository), Optional.empty());

		ReplicationEventService replicationEventService = new ReplicationEventServiceImpl(new ReplicationConfig(),
				replicationEventRepository, Optional.of(Arrays.asList(service)));

		ReplicationEvent replicationEvent = new ReplicationEvent(1, LocalDateTime.now(), TestMessage.class.getName(),
				EventType.UPDATE, "default",
				new ObjectMapper().findAndRegisterModules().writeValueAsString(testMessage));

		replicationEventService.processEvent(replicationEvent);

		Mockito.verify(testMessageRepository).save(Mockito.any());
	}

	@Test
	public void testUnknownObjectClass() throws JsonProcessingException {
		setup();

		ReplicationEventService replicationEventService = new ReplicationEventServiceImpl(new ReplicationConfig(),
				replicationEventRepository, Optional.of(Arrays.asList(service)));

		ReplicationEvent replicationEvent = new ReplicationEvent(1, LocalDateTime.now(), "unknown-class",
				EventType.UPDATE, "default",
				new ObjectMapper().findAndRegisterModules().writeValueAsString(testMessage));

		replicationEventService.processEvent(replicationEvent);

		Mockito.verify(replicationEventRepository, Mockito.never()).save(Mockito.any());
		Mockito.verify(testMessageRepository, Mockito.never()).save(Mockito.any());
	}

	@Test(expected = ReplicationUnmarshallingException.class)
	public void testUnmarshallingExceptionHandling() {
		setup();

		ReplicationEventService replicationEventService = new ReplicationEventServiceImpl(new ReplicationConfig(),
				replicationEventRepository, Optional.of(Arrays.asList(service)));

		ReplicationEvent replicationEvent = new ReplicationEvent(1, LocalDateTime.now(), TestMessage.class.getName(),
				EventType.UPDATE, "default", ") {{}}{{");

		replicationEventService.processEvent(replicationEvent);
	}

	@Test
	public void testSkipEventProcessing() throws JsonProcessingException {
		setup();

		service = new TestMessageReplicationService(true, new ObjectMapper().findAndRegisterModules(),
				this.testMessageRepository, replicationConfig, this.replicationEventRepository, replicationSource);

		ReplicationEventService replicationEventService = new ReplicationEventServiceImpl(new ReplicationConfig(),
				replicationEventRepository, Optional.of(Arrays.asList(service)));

		ReplicationEvent replicationEvent = new ReplicationEvent(1, LocalDateTime.now(), TestMessage.class.getName(),
				EventType.UPDATE, "default",
				new ObjectMapper().findAndRegisterModules().writeValueAsString(testMessage));

		replicationEventService.processEvent(replicationEvent);

		Mockito.verify(replicationEventRepository).save(Mockito.any());
		Mockito.verify(testMessageRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void testWithoutConfiguredReplicationService() throws JsonProcessingException {
		setup();

		ReplicationEventService replicationEventService = new ReplicationEventServiceImpl(new ReplicationConfig(),
				replicationEventRepository, Optional.empty());

		ReplicationEvent replicationEvent = new ReplicationEvent(1, LocalDateTime.now(), TestMessage.class.getName(),
				EventType.UPDATE, "default",
				new ObjectMapper().findAndRegisterModules().writeValueAsString(testMessage));

		replicationEventService.processEvent(replicationEvent);

		Mockito.verify(replicationEventRepository, Mockito.never()).save(Mockito.any());
		Mockito.verify(testMessageRepository, Mockito.never()).save(Mockito.any());
	}
}
