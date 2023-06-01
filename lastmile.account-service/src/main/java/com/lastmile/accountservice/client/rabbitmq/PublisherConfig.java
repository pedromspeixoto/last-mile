package com.lastmile.accountservice.client.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PublisherConfig {

    @Value("${rabbitmq.exchange.name}")
    private String commExchangeName;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(commExchangeName, true, false);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper mapper = (new ObjectMapper()).findAndRegisterModules();
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(mapper);
        jackson2JsonMessageConverter.setCreateMessageIds(true);
        return jackson2JsonMessageConverter;
    }
}