package io.daniellavoie.spring.replication.exception;

public class ReplicationMarshallingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ReplicationMarshallingException(Throwable cause) {
		super("Failed to marshall payload.", cause);
	}
}
