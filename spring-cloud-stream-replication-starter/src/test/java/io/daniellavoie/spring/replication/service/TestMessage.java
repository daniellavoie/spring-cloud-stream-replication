package io.daniellavoie.spring.replication.service;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestMessage {
	private long id;
	private String message;
	private String idxEncryptionKey;

	public TestMessage() {

	}

	public TestMessage(long id, String message, String idxEncryptionKey) {
		this.id = id;
		this.message = message;
		this.idxEncryptionKey = idxEncryptionKey;
	}

	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIdxEncryptionKey() {
		return idxEncryptionKey;
	}

	public void setIdxEncryptionKey(String idxEncryptionKey) {
		this.idxEncryptionKey = idxEncryptionKey;
	}
}
