package io.daniellavoie.spring.replication.sample;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.spring.replication.AbstractReplicationService;
import io.daniellavoie.spring.replication.ReplicationConfig;
import io.daniellavoie.spring.replication.repository.ReplicationEventRepository;

@Service
public class CustomerServiceImpl extends AbstractReplicationService<Customer> implements CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository, ReplicationConfig replicationConfig,
			ReplicationEventRepository replicationEventRepository, ReplicationSource replicationSource) {
		super(false, new ObjectMapper().findAndRegisterModules(), replicationConfig, replicationEventRepository,
				replicationSource);

		this.customerRepository = customerRepository;
	}

	public void delete(long id) {
		customerRepository.deleteById(id);
		
		sendDeleteEvent(String.valueOf(id));
	}

	@Override
	public Customer save(Customer customer) {
		customer = customerRepository.save(customer);

		sendUpdateEvent(customer);

		return customer;
	}

	@Override
	public void processUpdateEvent(Customer customer) {
		customerRepository.save(customer);
	}

	@Override
	public void processDelete(String serializedId) {
		customerRepository.deleteById(Long.parseLong(serializedId));
	}

	@Override
	public Class<Customer> getEntityClass() {
		return Customer.class;
	}
}
