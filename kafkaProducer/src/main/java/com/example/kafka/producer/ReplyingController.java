package com.example.kafka.producer;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyTypedMessageFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.producer.data.Employee;
import com.example.kafka.producer.data.EmployeeResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ReplyingController {

	@Autowired
	private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;
	
	@RequestMapping("/reply/{name}")
	public EmployeeResult replying(@PathVariable("name") String name) throws InterruptedException, ExecutionException {
		String id = UUID.randomUUID().toString();
		Employee employee = new Employee();
		employee.setId(id);
		employee.setName(name);
		
		log.info("request: {}", name);
		
		Message<Employee> message = MessageBuilder.withPayload(employee)
					.setHeader(KafkaHeaders.TOPIC, "topic-send")
					.setHeader(KafkaHeaders.KEY, id)
					.setHeader(KafkaHeaders.REPLY_TOPIC, "topic-result")
					.build();
		
		RequestReplyTypedMessageFuture<String, Object, EmployeeResult> future = 
				replyingKafkaTemplate.sendAndReceive(message, new ParameterizedTypeReference<EmployeeResult>() {});
		
		log.info("send message: {}", employee);
		
		Message<EmployeeResult> resultMessage =  future.get();
		
		return resultMessage.getPayload();		
	}
}
