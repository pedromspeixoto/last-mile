package com.lastmile.orderengine.config.rabbitmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.StringJoiner;

@Configuration
public class DeadLetterProperties {

    @Value("${rabbitmq.dead-letter.name}")
    private String deadLetter;

    @Value("{rabbit.top.dead-letter.routing-key")
    private String topDeadLetterRoutingKey;

    public String getDeadLetter() {
        return deadLetter;
    }

    public String getTopDeadLetterRoutingKey() {
        return topDeadLetterRoutingKey;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DeadLetterProperties.class.getSimpleName() + "[", "]")
                .add("deadLetter='" + deadLetter + "'")
                .add("topDeadLetterRoutingKey='" + topDeadLetterRoutingKey + "'")
                .toString();
    }
}