package io.daniellavoie.spring.replication.exception;

public class ReplicationUnmarshallingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ReplicationUnmarshallingException(Throwable cause) {
		super("Failed to unmarshall payload.", cause);
	}
}
