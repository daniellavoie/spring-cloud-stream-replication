package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface ReplicationEventService {
	long count();

	void deleteAll();

	Stream<ReplicationEvent> findAll();

	void processEvent(ReplicationEvent replicationEvent);

	void recoverEvents(String source, LocalDateTime since);

}
