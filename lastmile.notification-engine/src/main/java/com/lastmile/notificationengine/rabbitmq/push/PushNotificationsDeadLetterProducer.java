package com.lastmile.notificationengine.rabbitmq.push;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lastmile.notificationengine.config.ServiceProperties;
import com.lastmile.notificationengine.config.rabbitmq.dlq.DeadLetterProperties;
import com.lastmile.notificationengine.config.rabbitmq.push.PushNotificationsListenerConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class PushNotificationsDeadLetterProducer {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange deadLetterExchange;
    private final ServiceProperties serviceProperties;
    private final PushNotificationsListenerConfig pushListenerProperties;
    private final DeadLetterProperties deadLetterProperties;

    @Autowired
    public PushNotificationsDeadLetterProducer(AmqpAdmin amqpAdmin,
                                 RabbitTemplate rabbitTemplate,
                                 DirectExchange deadLetterExchange,
                                 ServiceProperties serviceProperties,
                                 PushNotificationsListenerConfig pushListenerProperties,
                                 DeadLetterProperties deadLetterProperties) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
        this.deadLetterExchange = deadLetterExchange;
        this.serviceProperties = serviceProperties;
        this.pushListenerProperties = pushListenerProperties;
        this.deadLetterProperties = deadLetterProperties;
    }

    public Queue pushDeadLetterQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", this.getQueueName());
        return new Queue(this.getQueueName(), false, false, false, args);
    }

    @PostConstruct
    public void amqpDeclarations() {
        this.amqpAdmin.declareQueue(this.pushDeadLetterQueue());
        this.amqpAdmin.declareExchange(this.deadLetterExchange);
        this.amqpAdmin.declareBinding(BindingBuilder.bind(this.pushDeadLetterQueue()).to(this.deadLetterExchange).with(this.deadLetterProperties.getPushDeadLetterRoutingKey()));

    }

    public void send(Object message) {
        rabbitTemplate.convertAndSend(this.deadLetterExchange.getName(), this.deadLetterProperties.getPushDeadLetterRoutingKey(), message);
    }

    public String getQueueName(){
        return this.serviceProperties.getEnvironment() + "-" + this.serviceProperties.getName() + "-"
               + this.pushListenerProperties.getQueue() + "-" + this.deadLetterProperties.getDeadLetter();
    }

}