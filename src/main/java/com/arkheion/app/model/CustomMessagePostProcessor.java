package com.arkheion.app.model;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class CustomMessagePostProcessor implements MessagePostProcessor{

	private final Long ttl;

    public CustomMessagePostProcessor(final Long ttl) {
        this.ttl = ttl;
    }
	
	@Override
	public Message postProcessMessage(Message message) throws AmqpException {
		message.getMessageProperties().getHeaders().put("x-message-ttl", ttl.toString());
        return message;
	}

}
