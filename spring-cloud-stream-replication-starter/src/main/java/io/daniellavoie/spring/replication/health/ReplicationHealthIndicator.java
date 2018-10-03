package io.daniellavoie.spring.replication.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

public class ReplicationHealthIndicator extends AbstractHealthIndicator {
	private ReplicationHealthCheckService replicationHealthCheckService;
	
	public ReplicationHealthIndicator(ReplicationHealthCheckService replicationHealthCheckService) {
		this.replicationHealthCheckService = replicationHealthCheckService;
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.withDetail("replicationHealth", replicationHealthCheckService.getReplicationHealth()).up();
	}
}
