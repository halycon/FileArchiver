package com.arkheion.app;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.arkheion.app.model.Priority;
import com.sun.xml.ws.transport.http.servlet.WSSpringServlet;

@SpringBootApplication
@ImportResource("classpath:spring-core-config.xml")
public class ArchiveApplication extends SpringBootServletInitializer implements RabbitListenerConfigurer {
	
	public static final String EXCHANGE_NAME = "appExchange";
	
	public static final String ROUTING_KEY = "messages.key";
	
	@Value("${spring.rabbitmq.host}")
	private String rabbitMqHostName;
	
	@Value("${spring.rabbitmq.username}")
	private String rabbitMqUsername;
	
	@Value("${spring.rabbitmq.password}")
	private String rabbitMqPassword;

	
	public static void main(String[] args) {
		new ArchiveApplication().configure(new SpringApplicationBuilder(ArchiveApplication.class)).run(args);
	}	

	@Bean
	public ServletRegistrationBean<WSSpringServlet> servletRegistrationBean() {
		return new ServletRegistrationBean<WSSpringServlet>(new WSSpringServlet(), "/FileArchiverSoap");
	}
	
	@Bean
	public TopicExchange appExchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}

	@Bean
	public Queue appQueueGENERAL() {
		return new Queue(Priority.GENERAL.toString());
	}

	@Bean
	public Queue appQueueLEVEL1() {
		return new Queue(Priority.LEVEL1.toString());
	}
	
	@Bean
	public Queue appQueueLEVEL2() {
		return new Queue(Priority.LEVEL2.toString());
	}
	
	@Bean
	public Queue appQueueLEVEL3() {
		return new Queue(Priority.LEVEL3.toString());
	}
	
	@Bean
	public Queue appQueueLEVEL4() {
		return new Queue(Priority.LEVEL4.toString());
	}
	
	@Bean
	public Queue appQueueLEVEL5() {
		return new Queue(Priority.LEVEL5.toString());
	}
	
	@Bean
	public Queue appQueueTEST() {
		return new Queue(Priority.TEST.toString());
	}

	@Bean
	public Binding declareBindingGENERAL() {
		return BindingBuilder.bind(appQueueGENERAL()).to(appExchange()).with(ROUTING_KEY);
	}

	@Bean
	public Binding declareBindingLEVEL1() {
		return BindingBuilder.bind(appQueueLEVEL1()).to(appExchange()).with(ROUTING_KEY);
	}
	
	@Bean
	public Binding declareBindingLEVEL2() {
		return BindingBuilder.bind(appQueueLEVEL2()).to(appExchange()).with(ROUTING_KEY);
	}
	
	@Bean
	public Binding declareBindingLEVEL3() {
		return BindingBuilder.bind(appQueueLEVEL3()).to(appExchange()).with(ROUTING_KEY);
	}
	
	@Bean
	public Binding declareBindingLEVEL4() {
		return BindingBuilder.bind(appQueueLEVEL4()).to(appExchange()).with(ROUTING_KEY);
	}
	
	@Bean
	public Binding declareBindingLEVEL5() {
		return BindingBuilder.bind(appQueueLEVEL5()).to(appExchange()).with(ROUTING_KEY);
	}
	
	@Bean
	public Binding declareBindingTEST() {
		return BindingBuilder.bind(appQueueTEST()).to(appExchange()).with(ROUTING_KEY);
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate() {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
		return rabbitTemplate;
	}

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
		return new MappingJackson2MessageConverter();
	}

	@Bean
	public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
		DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
		factory.setMessageConverter(consumerJackson2MessageConverter());
		return factory;
	}
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqHostName);
		connectionFactory.setUsername(rabbitMqUsername);
		connectionFactory.setPassword(rabbitMqPassword);
		return connectionFactory;
}

	@Override
	public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
		registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
}

}
