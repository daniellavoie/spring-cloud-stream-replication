package io.daniellavoie.spring.replication;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class PurgeReplicationEventsConfig implements SchedulingConfigurer {
	private ReplicationConfig replicationConfig;
	private ReplicationEventService replicationEventService;
	private TaskScheduler taskExecutor;

	public PurgeReplicationEventsConfig(ReplicationConfig replicationConfig,
			ReplicationEventService replicationEventService, TaskScheduler taskExecutor) {
		this.replicationConfig = replicationConfig;
		this.replicationEventService = replicationEventService;
		this.taskExecutor = taskExecutor;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		if (replicationConfig.isPurgeEnabled()) {
			taskRegistrar.setScheduler(taskExecutor);
			taskRegistrar.addTriggerTask(() -> replicationEventService.purgeEvents(), this::computeNextExecution);
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
}
