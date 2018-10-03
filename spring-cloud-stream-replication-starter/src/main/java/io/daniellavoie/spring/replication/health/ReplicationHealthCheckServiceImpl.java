package io.daniellavoie.spring.replication.health;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.AbstractReplicationService;
import io.daniellavoie.spring.replication.ReplicationConfig;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

public class ReplicationHealthCheckServiceImpl extends AbstractReplicationService<ReplicationHealthCheck>
		implements ReplicationHealthCheckService, SchedulingConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationHealthCheckServiceImpl.class);
	
	public ReplicationHealthCheckServiceImpl(ObjectMapper objectMapper, ReplicationConfig replicationConfig,
			ReplicationEventRepository replicationEventRepository, ReplicationSource replicationSource,
			TaskExecutor taskExecutor) {
		super(false, objectMapper, replicationConfig, replicationEventRepository, replicationSource);

		this.replicationConfig = replicationConfig;
		this.taskExecutor = taskExecutor;
	}

	private ReplicationConfig replicationConfig;
	private TaskExecutor taskExecutor;

	private ReplicationHealth replicationHealth = new ReplicationHealth();

	@Override
	public ReplicationHealth getReplicationHealth() {
		return replicationHealth;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		if (replicationConfig.isHealthCheckEnabled()) {
			taskRegistrar.setScheduler(taskExecutor);
			taskRegistrar.addTriggerTask(() -> {
				LOGGER.trace("Sending a replication health check event.");
				
				replicationHealth
						.setRequest(new ReplicationHealthCheck(replicationConfig.getSource(), LocalDateTime.now()));
				
				sendUpdateEvent(replicationHealth.getRequest());
			}, this::computeNextExecution);
		}
	}

	private Date computeNextExecution(TriggerContext triggerContext) {
		Calendar nextExecutionTime = new GregorianCalendar();
		Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
		nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
		if (lastActualExecutionTime != null) {
			nextExecutionTime.add(Calendar.MILLISECOND, replicationConfig.getPurgeExecutionRateMillis());
		}
		return nextExecutionTime.getTime();
	}

	@Override
	public void processUpdateEvent(ReplicationHealthCheck replicationHealthCheck) {
		LOGGER.trace("Received a replication health check event.");
		
		this.replicationHealth.setResponse(replicationHealthCheck);
	}

	@Override
	public void processDelete(String serializedId) {
		// Shouldn't occur.
	}

	@Override
	public Class<ReplicationHealthCheck> getEntityClass() {
		return ReplicationHealthCheck.class;
	}

}
