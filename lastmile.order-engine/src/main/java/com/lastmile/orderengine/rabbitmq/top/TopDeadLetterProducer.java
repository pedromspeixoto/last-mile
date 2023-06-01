package com.lastmile.orderengine.rabbitmq.top;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lastmile.orderengine.config.ServiceProperties;
import com.lastmile.orderengine.config.rabbitmq.DeadLetterProperties;
import com.lastmile.orderengine.config.rabbitmq.top.TopListenerConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class TopDeadLetterProducer {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange deadLetterExchange;
    private final ServiceProperties serviceProperties;
    private final TopListenerConfig topListenerProperties;
    private final DeadLetterProperties deadLetterProperties;

    @Autowired
    public TopDeadLetterProducer(AmqpAdmin amqpAdmin,
                                 RabbitTemplate rabbitTemplate,
                                 DirectExchange deadLetterExchange,
                                 ServiceProperties serviceProperties,
                                 TopListenerConfig topListenerProperties,
                                 DeadLetterProperties deadLetterProperties) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
        this.deadLetterExchange = deadLetterExchange;
        this.serviceProperties = serviceProperties;
        this.topListenerProperties = topListenerProperties;
        this.deadLetterProperties = deadLetterProperties;
    }

    public Queue topDeadLetterQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", this.getQueueName());
        return new Queue(this.getQueueName(), false, false, false, args);
    }

    @PostConstruct
    public void amqpDeclarations() {
        this.amqpAdmin.declareQueue(this.topDeadLetterQueue());
        this.amqpAdmin.declareExchange(this.deadLetterExchange);
        this.amqpAdmin.declareBinding(BindingBuilder.bind(this.topDeadLetterQueue()).to(this.deadLetterExchange).with(this.deadLetterProperties.getTopDeadLetterRoutingKey()));

    }

    public void send(Object message) {
        rabbitTemplate.convertAndSend(this.deadLetterExchange.getName(), this.deadLetterProperties.getTopDeadLetterRoutingKey(), message);
    }

    public String getQueueName(){
        return this.serviceProperties.getEnvironment() + "-" + this.serviceProperties.getName() + "-"
                + this.topListenerProperties.getQueue() + "-" + this.deadLetterProperties.getDeadLetter();
    }

}