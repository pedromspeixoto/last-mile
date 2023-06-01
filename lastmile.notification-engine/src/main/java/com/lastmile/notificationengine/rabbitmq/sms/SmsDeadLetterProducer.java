package com.lastmile.notificationengine.rabbitmq.sms;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lastmile.notificationengine.config.ServiceProperties;
import com.lastmile.notificationengine.config.rabbitmq.dlq.DeadLetterProperties;
import com.lastmile.notificationengine.config.rabbitmq.sms.SmsListenerConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsDeadLetterProducer {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange deadLetterExchange;
    private final ServiceProperties serviceProperties;
    private final SmsListenerConfig textMessageListenerProperties;
    private final DeadLetterProperties deadLetterProperties;

    @Autowired
    public SmsDeadLetterProducer(AmqpAdmin amqpAdmin,
                                 RabbitTemplate rabbitTemplate,
                                 DirectExchange deadLetterExchange,
                                 ServiceProperties serviceProperties,
                                 SmsListenerConfig textMessageListenerProperties,
                                 DeadLetterProperties deadLetterProperties) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
        this.deadLetterExchange = deadLetterExchange;
        this.serviceProperties = serviceProperties;
        this.textMessageListenerProperties = textMessageListenerProperties;
        this.deadLetterProperties = deadLetterProperties;
    }

    public Queue smslDeadLetterQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", this.getQueueName());
        return new Queue(this.getQueueName(), false, false, false, args);
    }

    @PostConstruct
    public void amqpDeclarations() {
        this.amqpAdmin.declareQueue(this.smslDeadLetterQueue());
        this.amqpAdmin.declareExchange(this.deadLetterExchange);
        this.amqpAdmin.declareBinding(BindingBuilder.bind(this.smslDeadLetterQueue()).to(this.deadLetterExchange).with(this.deadLetterProperties.getSmsDeadLetterRoutingKey()));

    }

    public void send(Object message) {
        rabbitTemplate.convertAndSend(this.deadLetterExchange.getName(), this.deadLetterProperties.getSmsDeadLetterRoutingKey(), message);
    }

    public String getQueueName(){
        return this.serviceProperties.getEnvironment() + "-" + this.serviceProperties.getName() + "-"
                + this.textMessageListenerProperties.getQueue() + "-" + this.deadLetterProperties.getDeadLetter();
    }

}