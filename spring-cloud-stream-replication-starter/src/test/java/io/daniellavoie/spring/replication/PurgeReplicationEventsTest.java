package io.daniellavoie.spring.replication;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.daniellavoie.spring.replication.ReplicationEvent.EventType;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "replication.purge.enabled=false")
public class PurgeReplicationEventsTest {
	@Autowired
	private ReplicationEventService replicationEventService;

	@Autowired
	private ReplicationEventRepository replicationEventRepository;

	@Test
	public void test() {
		replicationEventRepository.saveAll(IntStream.range(1, 201)
				.mapToObj(index -> new ReplicationEvent(index,
						index < 101 ? LocalDateTime.now().minusMonths(1) : LocalDateTime.now(), "test-class",
						EventType.UPDATE, "default", "test-payload", "test-idx-key"))
				.collect(Collectors.toList()));

		Assert.assertEquals(100, replicationEventService.purgeEvents());
		Assert.assertEquals(100, replicationEventRepository.count());
	}
}
