package io.daniellavoie.spring.replication;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.AbstractReplicationService.ReplicationSource;
import io.daniellavoie.spring.replication.ReplicationEvent.EventType;
import io.daniellavoie.spring.replication.exception.ReplicationMarshallingException;
import io.daniellavoie.spring.replication.exception.ReplicationUnmarshallingException;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

@EnableBinding(ReplicationSource.class)
public abstract class AbstractReplicationService<T> implements ReplicationService<T> {
	public interface ReplicationSource {
		static final String OUTPUT = "replication-source";

		@Output(OUTPUT)
		MessageChannel output();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReplicationService.class);

	private final boolean skipEventProcessing;
	private final ObjectMapper objectMapper;
	private final ReplicationConfig replicationConfig;
	private final ReplicationEventRepository replicationEventRepository;
	private final ReplicationSource replicationSource;

	private final String entityClass;

	public AbstractReplicationService(boolean skipEventProcessing, ObjectMapper objectMapper,
			ReplicationConfig replicationConfig, ReplicationEventRepository replicationEventRepository,
			ReplicationSource replicationSource) {
		this.skipEventProcessing = skipEventProcessing;
		this.objectMapper = objectMapper;
		this.replicationConfig = replicationConfig;
		this.replicationEventRepository = replicationEventRepository;
		this.replicationSource = replicationSource;

		this.entityClass = getEntityClass().getName();
	}

	public void convertAndProcessUpdateEvent(ReplicationEvent replicationEvent) {
		try {
			processUpdateEvent(objectMapper.readValue(replicationEvent.getPayload(), getEntityClass()));
		} catch (IOException ex) {
			throw new ReplicationUnmarshallingException(ex);
		}
	}

	public abstract Class<T> getEntityClass();

	@Override
	public boolean skipEventProcessing() {
		return skipEventProcessing;
	}

	public void sendDeleteEvent(String serializedId) {
		ReplicationEvent replicationEvent = replicationEventRepository.save(new ReplicationEvent(0l,
				LocalDateTime.now(), entityClass, EventType.DELETE, replicationConfig.getSource(), serializedId));

		if (replicationConfig.isEnabled()) {
			replicationSource.output().send(MessageBuilder.withPayload(replicationEvent).build());
		}
	}

	@Override
	public void sendUpdateEvent(Object payload) {
		try {
			ReplicationEvent replicationEvent = replicationEventRepository
					.save(new ReplicationEvent(0l, LocalDateTime.now(), entityClass, EventType.UPDATE,
							replicationConfig.getSource(), objectMapper.writeValueAsString(payload)));

			if (replicationConfig.isEnabled()) {
				replicationSource.output().send(MessageBuilder.withPayload(replicationEvent).build());
			}
		} catch (JsonProcessingException e) {
			LOGGER.debug("Could not marshall payload : " + payload);

			throw new ReplicationMarshallingException(e);
		}
	}
}
