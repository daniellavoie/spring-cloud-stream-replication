package io.daniellavoie.spring.replication;

public interface ReplicationService<T> {
	void convertAndProcessUpdateEvent(ReplicationEvent replicationEvent);

	boolean skipEventProcessing();
	
	Class<T> getEntityClass();

	void processUpdateEvent(T entity);

	void processDelete(String serializedId);

	void sendUpdateEvent(Object payload);
}
