package io.daniellavoie.spring.replication.service;

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
		testMessageRepository.delete(id);

		sendDeleteEvent(String.valueOf(id));
	}

	public TestMessage save(TestMessage testMessage) {
		testMessage = testMessageRepository.save(testMessage);

		sendUpdateEvent(testMessage);

		return testMessage;

	}

	@Override
	public void processUpdateEvent(TestMessage testMessage) {
		testMessageRepository.save(testMessage);
	}

	@Override
	public void processDelete(String serializedId) {
		testMessageRepository.delete(Long.valueOf(serializedId));
	}

	@Override
	public Class<TestMessage> getEntityClass() {
		return TestMessage.class;
	}
}
