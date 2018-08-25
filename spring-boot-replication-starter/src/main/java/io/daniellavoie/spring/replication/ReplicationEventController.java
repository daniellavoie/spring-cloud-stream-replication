package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/replication-event")
public class ReplicationEventController {
	private ReplicationEventService replicationEventService;

	public ReplicationEventController(ReplicationEventService replicationEventService) {
		this.replicationEventService = replicationEventService;
	}

	@DeleteMapping
	public void deleteAll() {
		replicationEventService.deleteAll();
	}

	@PostMapping
	public void recoverEvents(@RequestParam String source,
			@RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime since) {
		replicationEventService.recoverEvents(source, since);
	}
}
