package com.lastmile.accountservice.client.rabbitmq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class EventPublisherConfig {

    @Autowired
    public ConnectionFactory connectionFactory;
    @Autowired
    public DirectExchange exchange;
    @Autowired
    public MessageConverter messageConverter;
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Bean
    public AmqpTemplate communicationExchangeConnector(ConnectionFactory connectionFactory,
                                                       MessageConverter messageConverter,
                                                       DirectExchange exchange) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(exchange.getName());
        return rabbitTemplate;
    }

    @PostConstruct
    public void amqpDeclarations() {
        this.amqpAdmin.declareExchange(this.exchange);
    }
}