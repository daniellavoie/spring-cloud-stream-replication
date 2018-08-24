package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping
	public void recoverEvents(@RequestParam String source, LocalDateTime since) {
		replicationEventService.recoverEvents(source, since);
	}
}
