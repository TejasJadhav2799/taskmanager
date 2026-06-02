package com.thinkalike.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.thinkalike.taskmanager.model")
@EnableJpaRepositories("com.thinkalike.taskmanager.repository")
@EnableCaching
// @EnableCaching activates @Cacheable, @CacheEvict, @CachePut
// without this annotation those annotations do nothing
@SpringBootApplication
public class TaskmanagerApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(TaskmanagerApplication.class, args);

		// prints every bean Spring found — if User-related beans appear, scanning works
		System.out.println("=== BEANS LOADED: " + ctx.getBeanDefinitionCount() + " ===");

		// check if our Kafka beans are registered
		System.out.println("TaskEventProducer bean exists: " +
				ctx.containsBean("taskEventProducer"));
		System.out.println("TaskEventConsumer bean exists: " +
				ctx.containsBean("taskEventConsumer"));
	}
}
