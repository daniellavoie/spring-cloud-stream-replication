# Spring Replication

Provides an abstraction to replicate event accros different sites using Spring Cloud Stream.

## Features

* Auto configured event store all entities you need to replicate.
* Re-process events with an auto configured endpoint.
* Support for `update` and `delete` operations. 

## Getting started

Add the following dependency to your project

```
      <dependency>
	  <groupId>io.daniellavoie.spring.replication</groupId>
	  <artifactId>spring-boot-replication-starter</artifactId>
	  <version>0.0.1-SNAPSHOT</version>
	</dependency>
```

## AbstractReplicationService


