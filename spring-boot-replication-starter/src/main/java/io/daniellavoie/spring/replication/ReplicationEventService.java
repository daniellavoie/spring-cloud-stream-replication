package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;

public interface ReplicationEventService {
	long count();

	void deleteAll();

	void processEvent(ReplicationEvent replicationEvent);

	void recoverEvents(String source, LocalDateTime since);

}
