package com.lastmile.orderengine.config.rabbitmq.top;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class TopListenerConfig {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.profiles.active}")
    private String environment;

	@Value("${rabbitmq.top.queue}")
	private String queue;

	@Value("${rabbitmq.top.durable}")
	private Boolean durable;

	@Value("${rabbitmq.top.routing-keys}")
	private List<String> routingKeys;

    private final AmqpAdmin amqpAdmin;
    public ConnectionFactory connectionFactory;
    public DirectExchange exchange;
    public MessageConverter messageConverter;
    private final RetryOperationsInterceptor topDeadLetterInterceptor;

    public TopListenerConfig(AmqpAdmin amqpAdmin,
                             ConnectionFactory connectionFactory, DirectExchange exchange, MessageConverter messageConverter,
                             @Qualifier("topDeadLetterInterceptor") RetryOperationsInterceptor topDeadLetterInterceptor) {

        this.amqpAdmin = amqpAdmin;
        this.connectionFactory = connectionFactory;
        this.exchange = exchange;
        this.messageConverter = messageConverter;
        this.topDeadLetterInterceptor = topDeadLetterInterceptor;
    }

    @Bean
    public Queue topQueue() {
        return new Queue(environment + "-" + name + "-" + queue, durable);
    }

    @Bean
    public List<Binding> topQueueBindings() {

        List<Binding> bindings = new ArrayList<Binding>();

        for (String routingKey : routingKeys) {
            bindings.add(BindingBuilder.bind(this.topQueue()).to(this.exchange).with(routingKey));
        }

        return bindings;

    }

    @Bean
    public SimpleRabbitListenerContainerFactory topListenerFactory() {

        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(this.connectionFactory);
        containerFactory.setMaxConcurrentConsumers(10);
        containerFactory.setConcurrentConsumers(1);
        containerFactory.setAutoStartup(true);
        containerFactory.setMessageConverter(this.messageConverter);
        containerFactory.setPrefetchCount(10);
        // containerFactory.setDefaultRequeueRejected(true);
        containerFactory.setAdviceChain(topDeadLetterInterceptor);

        return containerFactory;

    }

    @PostConstruct
    public void amqpDeclarations() {

        this.amqpAdmin.declareQueue(this.topQueue());
        this.amqpAdmin.declareExchange(this.exchange);

        for (Binding binding : this.topQueueBindings()) {
            this.amqpAdmin.declareBinding(binding);
        }

    }

    public String getQueue() {
        return this.queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

}