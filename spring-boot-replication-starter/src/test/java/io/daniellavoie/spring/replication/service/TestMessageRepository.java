package io.daniellavoie.spring.replication.service;

public interface TestMessageRepository {
	TestMessage save(TestMessage testMessage);

	void delete(long id);
}