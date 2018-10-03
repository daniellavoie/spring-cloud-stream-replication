package io.daniellavoie.spring.replication.health;

import java.time.LocalDateTime;

public class ReplicationHealthCheck {
	private String source;
	private LocalDateTime timestamp;
	
	public ReplicationHealthCheck() {
		
	}
	
	public ReplicationHealthCheck(String source, LocalDateTime timestamp) {
		this.source = source;
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}}
