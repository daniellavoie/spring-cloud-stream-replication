package io.daniellavoie.springreplication.service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestMessageRepository extends JpaRepository<TestMessage, Long> {

}