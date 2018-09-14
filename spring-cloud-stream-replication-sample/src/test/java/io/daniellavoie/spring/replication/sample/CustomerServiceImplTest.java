package io.daniellavoie.spring.replication.sample;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.daniellavoie.spring.replication.ReplicationEventService;

@SpringBootTest(properties = "replication.enabled=false")
@RunWith(SpringRunner.class)
public class CustomerServiceImplTest {
	@Autowired
	private CustomerService customerService;

	@Autowired
	private ReplicationEventService replicationEventService;

	@Test
	public void testSave() {
		Customer customer = new Customer(1l, "Daniel Lavoie");

		long previousCount = replicationEventService.count();

		customerService.save(customer);

		Assert.assertEquals(previousCount + 1, replicationEventService.count());
	}
}
