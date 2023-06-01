package com.lastmile.orderengine.config.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class DeadLetterConfiguration {


    private final ConnectionFactory connectionFactory;

    @Autowired
    public DeadLetterConfiguration(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy());
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }

    @Bean
    public ExponentialBackOffPolicy exponentialBackOffPolicy() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(10000);
        backOffPolicy.setMultiplier(1.0);
        backOffPolicy.setMaxInterval(43200000);
        return backOffPolicy;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate messageQueueManager = new RabbitTemplate(connectionFactory);
        messageQueueManager.setRetryTemplate(this.retryTemplate());
        return messageQueueManager;
    }
}