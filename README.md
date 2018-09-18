# Replication for Spring Cloud Stream 

Provides an abstraction to replicate event across different sites using Spring Cloud Stream.

## Features

* Auto configured event store for all entities you need to replicate.
* Re-process events with an auto configured endpoint.
* Support for `update` and `delete` operations.

### Replicate and store data events across sites

![Replicate and store data events across sites](doc/images/replicate-and-store-data-events-across-sites.png)


### Reprocess events from a site

![Reprocess events from a site](doc/images/reprocess-events-from-a-site.png)

## Getting started

### Dependencies

Add the following dependency to your project

```
      <dependency>
	  <groupId>io.daniellavoie.spring.replication</groupId>
	  <artifactId>spring-boot-replication-starter</artifactId>
	  <version>0.0.2-SNAPSHOT</version>
	</dependency>
```

### Prepare your database with the event store

The project will store all event to be replicated inside a table named `replication_event`. You have to create that table within the schema of your application. We recommend packaging the creation of that table with [Flyway](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html#howto-execute-flyway-database-migrations-on-startup) or [Liquibase](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html#howto-execute-liquibase-database-migrations-on-startup). We provide scripts for different database :

* [h2](doc/sql-scripts/h2.sql)
* [Mysql](doc/sql-scripts/mysql.sql)
* [PostgreSQL](doc/sql-scripts/postgresql.sql) 

### Write your first replicated data service

Data replication is possible by extending `AbstractReplicationService`. This class provides the following features :

* Event notification through the `processUpdateEvent` and `processDelete` callback methods for the data type specified.
* Ability to generate replication event by calling the `sendUpdateEvent` method.

The project extends Spring Cloud Stream and ensure that all messages are consumed and published through a `replication-sink` and `replication-source` channel.
All dependencies required by the `AbstractReplicationService` constructor are provided by Auto Configuration and are ready to be injected in the constructor of your own implementations.

```
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
```

### Configure Messaging replication across sites.

#### RabbitMQ

To be completed