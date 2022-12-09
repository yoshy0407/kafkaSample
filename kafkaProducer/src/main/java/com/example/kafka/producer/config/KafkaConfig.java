package com.example.kafka.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.GenericMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {

	@Bean
	GenericMessageListenerContainer<String, Object> messageListenerContainer(
			ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
		ConcurrentMessageListenerContainer<String, Object> container = 
				containerFactory.createContainer("dummy");
		container.getContainerProperties().setGroupId("producer");
		container.setAutoStartup(false);
		return container;
	}
	
	@Bean
	ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate(
			ProducerFactory<String, Object> producerFactory,
			GenericMessageListenerContainer<String, Object> messageListenerContainer) {
		return new ReplyingKafkaTemplate<>(producerFactory, messageListenerContainer);
	}
	
}
