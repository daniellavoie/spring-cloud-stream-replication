package io.daniellavoie.spring.replication;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.daniellavoie.spring.replication.AbstractReplicationService.ReplicationSource;
import io.daniellavoie.spring.replication.ReplicationEventServiceImpl.ReplicationSink;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

@Configuration
@AutoConfigurationPackage
@EnableBinding({ ReplicationSink.class, ReplicationSource.class })
public class ReplicationAutoConfiguration {
	@Bean
	public ReplicationConfig replicationConfig() {
		return new ReplicationConfig();
	}

	@Bean
	public ReplicationEventService replicationEventService(ReplicationEventRepository replicationEventRepository,
			Optional<List<ReplicationService<?>>> replicationServices) {
		return new ReplicationEventServiceImpl(replicationEventRepository, replicationServices);
	}
}
