package com.lastmile.notificationengine.config.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lastmile.notificationengine.config.ServiceProperties;
import com.lastmile.notificationengine.config.rabbitmq.dlq.DeadLetterProperties;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {

    ObjectMapper objectMapper;
    private final ServiceProperties serviceProperties;
    private final DeadLetterProperties deadLetterProperties;

    @Autowired
    public ListenerConfig(ObjectMapper objectMapper,ServiceProperties serviceProperties, DeadLetterProperties deadLetterProperties) {
        this.objectMapper = objectMapper;
        this.serviceProperties = serviceProperties;
        this.deadLetterProperties = deadLetterProperties;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(this.serviceProperties.getEnvironment());
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(this.serviceProperties.getEnvironment() + "-" + deadLetterProperties.getDeadLetter());
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper mapper = objectMapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }

}