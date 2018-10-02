package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class ReplicationEvent {
	public enum EventType {
		UPDATE, DELETE
	}

	private long idReplicationEvent;
	private LocalDateTime timestamp;
	private String objectClass;
	private EventType eventType;
	private String source;
	private String payload;

	public ReplicationEvent() {

	}

	public ReplicationEvent(long idReplicationEvent, LocalDateTime timestamp, String objectClass, EventType eventType, String source,
			String payload) {
		setIdReplicationEvent(idReplicationEvent);
		setTimestamp(timestamp);
		setObjectClass(objectClass);
		setEventType(eventType);
		setSource(source);
		setPayload(payload);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getIdReplicationEvent() {
		return idReplicationEvent;
	}

	public void setIdReplicationEvent(long idReplicationEvent) {
		this.idReplicationEvent = idReplicationEvent;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

	@Enumerated(EnumType.STRING)
	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Lob
	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
