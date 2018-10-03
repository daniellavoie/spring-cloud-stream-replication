package io.daniellavoie.spring.replication;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.AbstractReplicationService.ReplicationSource;
import io.daniellavoie.spring.replication.health.ReplicationHealthCheckService;
import io.daniellavoie.spring.replication.health.ReplicationHealthCheckServiceImpl;
import io.daniellavoie.spring.replication.health.ReplicationHealthIndicator;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

@Configuration
@ConditionalOnProperty(name = "replication.health-check.enabled", matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.AbstractHealthIndicator")
public class ReplicationHealthCheckAutoConfiguration {

	@Bean
	public ReplicationHealthIndicator replicationHealthIndicator(
			ReplicationHealthCheckService replicationHealthCheckService, ReplicationConfig replicationConfig) {
		return new ReplicationHealthIndicator(replicationHealthCheckService);
	}

	@Bean
	public ReplicationHealthCheckService replicationHealthCheckService(ObjectMapper objectMapper,
			ReplicationConfig replicationConfig, ReplicationEventRepository replicationEventRepository,
			ReplicationSource replicationSource, TaskExecutor taskExecutor) {
		return new ReplicationHealthCheckServiceImpl(objectMapper, replicationConfig, replicationEventRepository,
				replicationSource, taskExecutor);
	}

}
