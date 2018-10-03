package io.daniellavoie.spring.replication.health;

public class ReplicationHealth {
	private ReplicationHealthCheck request;
	private ReplicationHealthCheck response;
	
	public ReplicationHealth() {
		
	}

	public ReplicationHealthCheck getRequest() {
		return request;
	}

	public void setRequest(ReplicationHealthCheck request) {
		this.request = request;
	}

	public ReplicationHealthCheck getResponse() {
		return response;
	}

	public void setResponse(ReplicationHealthCheck response) {
		this.response = response;
	}
}
