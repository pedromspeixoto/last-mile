package com.lastmile.orderservice.rabbitmq;

import com.lastmile.orderservice.dto.RabbitOrderEvent;
import com.lastmile.orderservice.dto.RabbitOrderModel;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    @Autowired
    private AmqpTemplate communicationExchangeConnector;

    // send order to rabbitmq broker (by default to top priority)
    public void sendOrderMessage(String routingKey, String orderIdentification,
                                 Double pickupLatitude, Double pickupLongitude,
                                 Double destinationLatitude, Double destinationLongitude, 
                                 Double orderValue) {
        RabbitOrderEvent object = new RabbitOrderEvent();
        RabbitOrderModel order = new RabbitOrderModel();
        order.setOrderIdentification(orderIdentification);
        order.setPickupLatitude(pickupLatitude);
        order.setPickupLongitude(pickupLongitude);
        order.setDestinationLatitude(destinationLatitude);
        order.setDestinationLongitude(destinationLongitude);
        order.setOrderValue(orderValue);
        object.setOrder(order);
        try {
            communicationExchangeConnector.convertAndSend(routingKey, object);
        } catch (AmqpException e) {
            throw e;
        }
    }

}