package com.lastmile.orderengine.config.rabbitmq.medium;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediumListenerConfig {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.profiles.active}")
    private String environment;

	@Value("${rabbitmq.medium.queue}")
	private String queue;

	@Value("${rabbitmq.medium.durable}")
	private Boolean durable;

	@Value("${rabbitmq.medium.routing-keys}")
	private List<String> routingKeys;


    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    public ConnectionFactory connectionFactory;

    @Autowired
    public DirectExchange exchange;

    @Autowired
    public MessageConverter messageConverter;

    @Bean
    public Queue mediumQueue() {
		return new Queue(environment + "-" + name + "-" + queue, durable);
    }

    @Bean
    public List<Binding> mediumQueueBindings() {

		List<Binding> bindings = new ArrayList<Binding>();

		for (String routingKey : routingKeys) {
			bindings.add(BindingBuilder.bind(this.mediumQueue()).to(this.exchange).with(routingKey));
		}

		return bindings;

    }

    @Bean
    public SimpleRabbitListenerContainerFactory mediumListenerFactory() {

		SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
		containerFactory.setConnectionFactory(this.connectionFactory);
		containerFactory.setMaxConcurrentConsumers(10);
		containerFactory.setConcurrentConsumers(1);
		containerFactory.setAutoStartup(true);
		containerFactory.setMessageConverter(this.messageConverter);
		containerFactory.setPrefetchCount(20);
		containerFactory.setDefaultRequeueRejected(true);

		return containerFactory;

    }

    @PostConstruct
    public void amqpDeclarations() {

		this.amqpAdmin.declareQueue(this.mediumQueue());
		this.amqpAdmin.declareExchange(this.exchange);

		for (Binding binding : this.mediumQueueBindings()) {
			this.amqpAdmin.declareBinding(binding);
		}

    }
}