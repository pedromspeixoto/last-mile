package com.lastmile.notificationengine.config.rabbitmq.sms;

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
public class SmsListenerConfig {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.profiles.active}")
    private String environment;

    @Value("${rabbitmq.sms.queue}")
    private String queue;

    @Value("${rabbitmq.sms.durable}")
    private Boolean durable;

    @Value("${rabbitmq.sms.routing-keys}")
    private List<String> routingKeys;

    private final AmqpAdmin amqpAdmin;
    public ConnectionFactory connectionFactory;
    public DirectExchange exchange;
    public MessageConverter messageConverter;
    private final RetryOperationsInterceptor smsDeadLetterInterceptor;

    public SmsListenerConfig(AmqpAdmin amqpAdmin,
                             ConnectionFactory connectionFactory, DirectExchange exchange, MessageConverter messageConverter,
                             @Qualifier("smsDeadLetterInterceptor") RetryOperationsInterceptor smsDeadLetterInterceptor) {

        this.amqpAdmin = amqpAdmin;
        this.connectionFactory = connectionFactory;
        this.exchange = exchange;
        this.messageConverter = messageConverter;
        this.smsDeadLetterInterceptor = smsDeadLetterInterceptor;
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(environment + "-" + name + "-" + queue, durable);
    }

    @Bean
    public List<Binding> smsQueueBindings() {

        List<Binding> bindings = new ArrayList<Binding>();

        for (String routingKey : routingKeys) {
            bindings.add(BindingBuilder.bind(this.smsQueue()).to(this.exchange).with(routingKey));
        }

        return bindings;

    }

    @Bean
    public SimpleRabbitListenerContainerFactory smsListenerFactory() {

        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(this.connectionFactory);
        containerFactory.setMaxConcurrentConsumers(10);
        containerFactory.setConcurrentConsumers(1);
        containerFactory.setAutoStartup(true);
        containerFactory.setMessageConverter(this.messageConverter);
        containerFactory.setPrefetchCount(10);
        // containerFactory.setDefaultRequeueRejected(true);
        containerFactory.setAdviceChain(smsDeadLetterInterceptor);

        return containerFactory;

    }

    @PostConstruct
    public void amqpDeclarations() {

        this.amqpAdmin.declareQueue(this.smsQueue());
        this.amqpAdmin.declareExchange(this.exchange);

        for (Binding binding : this.smsQueueBindings()) {
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