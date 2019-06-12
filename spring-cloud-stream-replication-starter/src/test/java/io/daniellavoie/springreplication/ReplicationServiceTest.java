package io.daniellavoie.springreplication;

import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.ReplicationConfig;
import io.daniellavoie.spring.replication.AbstractReplicationService.ReplicationSource;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;
import io.daniellavoie.springreplication.service.TestMessageReplicationService;
import io.daniellavoie.springreplication.service.TestMessageRepository;

public abstract class ReplicationServiceTest {
	TestMessageRepository testMessageRepository;
	ReplicationConfig replicationConfig;
	MessageChannel messageChannel;
	ReplicationSource replicationSource;
	ReplicationEventRepository replicationEventRepository;
	TestMessageReplicationService service;

	void setup() {
		setup(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	void setup(Optional<ObjectMapper> objectMapper, Optional<TestMessageRepository> testMessageRepository,
			Optional<MessageChannel> messageChannel, Optional<ReplicationEventRepository> replicationEventRepository,
			Optional<ReplicationConfig> replicationConfig) {
		this.testMessageRepository = testMessageRepository.orElseGet(() -> {
			TestMessageRepository repository = Mockito.mock(TestMessageRepository.class);
			Mockito.when(repository.save(Mockito.any())).then(invocation -> {
				return invocation.getArgument(0);
			});
			return repository;
		});

		this.replicationConfig = replicationConfig.orElseGet(() -> {
			ReplicationConfig config = new ReplicationConfig();
			config.setSource("default");

			return config;
		});

		this.messageChannel = messageChannel.orElseGet(() -> Mockito.mock(MessageChannel.class));

		replicationSource = Mockito.mock(ReplicationSource.class);
		Mockito.when(replicationSource.output()).thenReturn(this.messageChannel);

		this.replicationEventRepository = replicationEventRepository.orElseGet(() -> {
			ReplicationEventRepository repository = Mockito.mock(ReplicationEventRepository.class);

			Mockito.when(repository.save(Mockito.any())).then(invocation -> invocation.getArgument(0));

			return repository;
		});

		service = new TestMessageReplicationService(false,
				objectMapper.orElseGet(() -> new ObjectMapper().findAndRegisterModules()), this.testMessageRepository,
				this.replicationConfig, this.replicationEventRepository, replicationSource);
	}
}
