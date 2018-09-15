package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;

import io.daniellavoie.spring.replication.ReplicationEvent.EventType;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

public class ReplicationEventServiceImpl implements ReplicationEventService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationEventServiceImpl.class);

	interface ReplicationSink {
		String INPUT = "replication-sink";

		@Input(ReplicationSink.INPUT)
		SubscribableChannel input();
	}

	private ReplicationConfig replicationConfig;
	private ReplicationEventRepository replicationEventRepository;
	private final Map<String, ReplicationService<?>> replicationServices;

	public ReplicationEventServiceImpl(ReplicationConfig replicationConfig,
			ReplicationEventRepository replicationEventRepository,
			Optional<List<ReplicationService<?>>> replicationServices) {
		this.replicationConfig = replicationConfig;
		this.replicationEventRepository = replicationEventRepository;
		this.replicationServices = replicationServices.orElseGet(() -> Arrays.asList()).stream()
				.collect(Collectors.toMap(service -> service.getEntityClass().getName(), service -> service));
	}

	@Override
	public long count() {
		return replicationEventRepository.count();
	}

	@Override
	public void deleteAll() {
		replicationEventRepository.deleteAll();
	}

	@Override
	@StreamListener(ReplicationSink.INPUT)
	public void processEvent(ReplicationEvent replicationEvent) {
		replicationEvent = replicationEventRepository.save(replicationEvent);

		ReplicationService<?> replicationService = replicationServices.get(replicationEvent.getObjectClass());

		if (replicationService != null) {
			if (!replicationService.skipEventProcessing()) {
				if (EventType.UPDATE.equals(replicationEvent.getEventType())) {
					replicationService.convertAndProcessUpdateEvent(replicationEvent);
				} else {
					replicationService.processDelete(replicationEvent.getPayload());
				}
			}
		} else {
			LOGGER.warn("Could not find a replication service for class " + replicationEvent.getObjectClass() + ".");
		}
	}

	@Override
	@Transactional
	public int purgeEvents() {
		LOGGER.debug("Purging replication events.");
		int deleteCount = replicationEventRepository
				.deleteByTimestampLessThan(LocalDateTime.now().minusDays(replicationConfig.getPurgeDaysToKeep()));

		if (deleteCount > 0) {
			LOGGER.info("Purged " + deleteCount + " replication events.");
		}

		LOGGER.debug("Replication event purge completed.");

		return deleteCount;
	}

	@Override
	@Transactional
	public void recoverEvents(String source, LocalDateTime since) {
		try (Stream<ReplicationEvent> stream = replicationEventRepository.findBySourceAndTimestampGreaterThan(source,
				since)) {
			stream.forEach(this::recoverEvent);
		}
	}

	private void recoverEvent(ReplicationEvent replicationEvent) {
		ReplicationService<?> replicationService = replicationServices.get(replicationEvent.getObjectClass());

		if (EventType.UPDATE.equals(replicationEvent.getEventType())) {
			replicationService.convertAndProcessUpdateEvent(replicationEvent);
		} else {
			replicationService.processDelete(replicationEvent.getPayload());
		}
	}
}
