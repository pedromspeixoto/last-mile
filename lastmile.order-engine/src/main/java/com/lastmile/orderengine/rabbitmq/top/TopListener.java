package com.lastmile.orderengine.rabbitmq.top;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import com.lastmile.orderengine.dto.RabbitOrderEvent;
import com.lastmile.orderengine.service.OrderService;
import com.lastmile.orderengine.service.exception.DriversNotFoundException;
import com.lastmile.orderengine.service.exception.FeignCommunicationException;
import com.lastmile.orderengine.service.exception.TemplateValidationException;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;

@Component
public class TopListener {

    @Autowired
    private OrderService orderService;

    private final Logger logger = LoggerFactory.getLogger(TopListener.class);

    @RabbitListener(queues = { "#{topQueue.name}" }, containerFactory = "topListenerFactory")
    public void handleEvent(final RabbitOrderEvent event, final Message message)
            throws InterruptedException, DriversNotFoundException {

	    final MessageProperties properties = message.getMessageProperties();

        logger.info("new event:" + properties.getReceivedRoutingKey()
                    + "; message_id:" + properties.getMessageId()
                    + "; correlation_id:" + properties.getCorrelationId() 
                    + "; redelivered:" + properties.getRedelivered());

        // set context
        ServiceContext serviceContext = new ServiceContext(properties.getMessageId(), 
                                                           properties.getMessageId(),
                                                           Constants.REQUEST_ORIGIN_RABBITMQ,
                                                           Constants.REQUEST_ORIGIN_RABBITMQ,
                                                           null,
                                                           Constants.REQUEST_ORIGIN_INTERNAL,
                                                           Constants.REQUEST_ORIGIN_ENTITY_ORDER,
                                                           event.getOrder().getOrderIdentification(),
                                                           null);

        try {
            orderService.assignOrder(serviceContext, event.getOrder());
        } catch (FeignCommunicationException e) {
            throw new ListenerExecutionFailedException(e.getMessage(), e, message);
        } catch (TemplateValidationException e) {
            throw new AmqpRejectAndDontRequeueException(e);
        } catch (MessagingException | IOException e) {
            throw new ListenerExecutionFailedException(e.getMessage(), e, message);
        } catch (DriversNotFoundException e) {
            throw new ListenerExecutionFailedException(e.getMessage(), e, message);
        }
    }

}