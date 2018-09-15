package io.daniellavoie.spring.replication.repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.daniellavoie.spring.replication.ReplicationEvent;

public interface ReplicationEventRepository extends JpaRepository<ReplicationEvent, Long> {
	int deleteByTimestampLessThan(LocalDateTime timestamps);
	
	@Query("SELECT replicationEvent FROM ReplicationEvent replicationEvent")
	Stream<ReplicationEvent> findAllStream();

	Stream<ReplicationEvent> findBySourceAndTimestampGreaterThan(String source, LocalDateTime since);
}
