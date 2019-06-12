package io.daniellavoie.springreplication.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.AbstractReplicationService;
import io.daniellavoie.spring.replication.ReplicationConfig;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

public class TestMessageReplicationService extends AbstractReplicationService<TestMessage> {
	private TestMessageRepository testMessageRepository;

	public TestMessageReplicationService(boolean ignoreEventsFromOtherSources, ObjectMapper objectMapper,
			TestMessageRepository testMessageRepository, ReplicationConfig replicationConfig,
			ReplicationEventRepository replicationEventRepository, ReplicationSource replicationSource) {
		super(ignoreEventsFromOtherSources, objectMapper, replicationConfig, replicationEventRepository,
				replicationSource);

		this.testMessageRepository = testMessageRepository;
	}

	public void delete(long id) {
		testMessageRepository.deleteById(id);

		sendDeleteEvent(String.valueOf(id));
	}

	private TestMessage merge(TestMessage existingTestMessage, TestMessage testMessage) {
		existingTestMessage.setMessage(testMessage.getMessage());

		return existingTestMessage;
	}

	public TestMessage save(TestMessage testMessage) {
		TestMessage testMessageToSave = testMessageRepository.findById(testMessage.getId())
				.map(existingTestMessage -> merge(existingTestMessage, testMessage)).orElse(testMessage);

		TestMessage savedTestMessage = testMessageRepository.save(testMessageToSave);

		sendUpdateEvent(savedTestMessage);

		return savedTestMessage;
	}

	@Override
	public void processUpdateEvent(TestMessage testMessage) {
		testMessageRepository.save(testMessage);
	}

	@Override
	public void processDelete(String serializedId) {
		testMessageRepository.deleteById(Long.valueOf(serializedId));
	}

	@Override
	public Class<TestMessage> getEntityClass() {
		return TestMessage.class;
	}
}
