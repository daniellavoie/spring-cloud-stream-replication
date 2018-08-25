package io.daniellavoie.spring.replication;

import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import io.daniellavoie.spring.replication.service.TestMessage;
import io.daniellavoie.spring.replication.service.TestMessageReplicationService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ReplicationEventControllerTest {
	@Autowired
	private TestMessageReplicationService testMessageReplicationService;

	@Autowired
	private ReplicationEventService replicationEventService;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void assertMessageCanBeRecovered() {
		IntStream.range(1, 101).mapToObj(index -> new TestMessage(index, "Message " + index))
				.forEach(testMessageReplicationService::save);

		testMessageReplicationService.delete(1);

		restTemplate.exchange("/replication-event?source={source}&since={since}", HttpMethod.POST, null, Void.class,
				"default", "2000-10-31T01:30:00.000-05:00").getBody();

		Assert.assertEquals(101, replicationEventService.count());

		restTemplate.delete("/replication-event");

		Assert.assertEquals(0, replicationEventService.count());

	}
}
