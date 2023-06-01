package com.lastmile.notificationengine.config.rabbitmq.dlq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeadLetterProperties {

    @Value("${rabbitmq.dead-letter.name}")
    private String deadLetter;

    @Value("${rabbitmq.sms.dead-letter.routing-key}")
    private String smsDeadLetterRoutingKey;

    @Value("${rabbitmq.push.dead-letter.routing-key}")
    private String pushDeadLetterRoutingKey;

    public String getPushDeadLetterRoutingKey() {
        return pushDeadLetterRoutingKey;
    }

    public String getSmsDeadLetterRoutingKey() {
        return smsDeadLetterRoutingKey;
    }

    public String getDeadLetter() {
        return deadLetter;
    }

}