package com.lastmile.orderengine.config.rabbitmq.top;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import com.lastmile.orderengine.config.rabbitmq.DeadLetterProperties;

@Configuration
public class TopDeadLetterConfiguration {

    private final DirectExchange deadLetterExchange;
    private final DeadLetterProperties deadLetterProperties;
    private final RabbitTemplate rabbitTemplate;
    private final ExponentialBackOffPolicy exponentialBackOffPolicy;

    @Autowired
    public TopDeadLetterConfiguration(DirectExchange deadLetterExchange,
                                      DeadLetterProperties deadLetterProperties,
                                      RabbitTemplate rabbitTemplate,
                                      ExponentialBackOffPolicy exponentialBackOffPolicy ) {
        this.deadLetterExchange = deadLetterExchange;
        this.deadLetterProperties = deadLetterProperties;
        this.rabbitTemplate = rabbitTemplate;
        this.exponentialBackOffPolicy = exponentialBackOffPolicy;
    }

    @Bean
    @Qualifier("topDeadLetterInterceptor")
    public RetryOperationsInterceptor topDeadLetterInterceptor() {
        return RetryInterceptorBuilder.stateless().maxAttempts(5).recoverer(
                new RepublishMessageRecoverer(this.rabbitTemplate, this.deadLetterExchange.getName(), 
                                              this.deadLetterProperties.getTopDeadLetterRoutingKey()))
                .backOffPolicy(this.exponentialBackOffPolicy).build();
    }

}