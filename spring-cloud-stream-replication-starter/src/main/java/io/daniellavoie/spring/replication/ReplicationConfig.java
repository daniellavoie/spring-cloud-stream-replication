package io.daniellavoie.spring.replication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("replication")
public class ReplicationConfig {
	private boolean enabled = true;

	@Value("${replication.purge.enabled:true}")
	private boolean purgeEnabled = true;

	@Value("${replication.purge.execution-rate-millis:60000}")
	private int purgeExecutionRateMillis = 60 * 1000;

	@Value("${replication.purge.days-to-keep:30}")
	private int purgeDaysToKeep = 30;
	
	@Value("${replication.health-check.enabled:true}")
	private boolean healthCheckEnabled = true;
	
	@Value("${replication.health-check.rate-millis:60000}")
	private int healthCheckRateMillis = 60 * 1000;

	private String source = "default-site";

	public ReplicationConfig() {

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isPurgeEnabled() {
		return purgeEnabled;
	}

	public void setPurgeEnabled(boolean purgeEnabled) {
		this.purgeEnabled = purgeEnabled;
	}

	public int getPurgeExecutionRateMillis() {
		return purgeExecutionRateMillis;
	}

	public void setPurgeExecutionRateMillis(int purgeExecutionRateMillis) {
		this.purgeExecutionRateMillis = purgeExecutionRateMillis;
	}

	public int getPurgeDaysToKeep() {
		return purgeDaysToKeep;
	}

	public void setPurgeDaysToKeep(int purgeDaysToKeep) {
		this.purgeDaysToKeep = purgeDaysToKeep;
	}

	public boolean isHealthCheckEnabled() {
		return healthCheckEnabled;
	}

	public void setHealthCheckEnabled(boolean healthCheckEnabled) {
		this.healthCheckEnabled = healthCheckEnabled;
	}

	public int getHealthCheckRateMillis() {
		return healthCheckRateMillis;
	}

	public void setHealthCheckRateMillis(int healthCheckRateMillis) {
		this.healthCheckRateMillis = healthCheckRateMillis;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
