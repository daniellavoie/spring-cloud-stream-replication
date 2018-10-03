package io.daniellavoie.spring.replication;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.daniellavoie.spring.replication.AbstractReplicationService.ReplicationSource;
import io.daniellavoie.spring.replication.ReplicationEventServiceImpl.ReplicationSink;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

@Configuration
@EnableScheduling
@AutoConfigurationPackage
@EnableBinding({ ReplicationSink.class, ReplicationSource.class })
@PropertySource("classpath:spring-cloud-stream.properties")
public class ReplicationAutoConfiguration {

	@Bean
	public ReplicationConfig replicationConfig() {
		return new ReplicationConfig();
	}

	@Bean
	public ReplicationEventService replicationEventService(ReplicationConfig replicationConfig,
			ReplicationEventRepository replicationEventRepository,
			Optional<List<ReplicationService<?>>> replicationServices) {
		return new ReplicationEventServiceImpl(replicationConfig, replicationEventRepository, replicationServices);
	}

	@Bean
	public ReplicationEventController replicationEventController(ReplicationEventService replicationEventService) {
		return new ReplicationEventController(replicationEventService);
	}
}
