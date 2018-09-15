package io.daniellavoie.spring.replication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.AbstractReplicationService.ReplicationSource;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;
import io.daniellavoie.spring.replication.service.TestMessageReplicationService;
import io.daniellavoie.spring.replication.service.TestMessageRepository;

@SpringBootApplication
public class TestApplication {
	@Bean
	public TestMessageReplicationService testMessageReplicationService(ReplicationConfig replicationConfig,
			TestMessageRepository testMessageRepository, ReplicationEventRepository replicationEventRepository,
			ReplicationSource replicationSource) {
		return new TestMessageReplicationService(true, new ObjectMapper(), testMessageRepository, replicationConfig,
				replicationEventRepository, replicationSource);
	}
}
