package io.daniellavoie.spring.replication;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("replication")
public class ReplicationConfig {
	private boolean enabled = true;
	private String source;

	public ReplicationConfig() {

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
