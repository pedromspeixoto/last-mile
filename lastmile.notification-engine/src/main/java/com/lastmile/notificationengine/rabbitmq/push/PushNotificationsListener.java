package com.lastmile.notificationengine.rabbitmq.push;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lastmile.notificationengine.dto.push.RabbitPushEvent;
import com.lastmile.notificationengine.service.PushNotificationService;
import com.lastmile.notificationengine.service.exception.ExternalCommunicationException;
import com.lastmile.notificationengine.service.exception.TemplateValidationException;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;

@Component
public class PushNotificationsListener {

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private CustomLogging logger;

    @RabbitListener(queues = { "#{pushQueue.name}" }, containerFactory = "pushListenerFactory")
    public void handleEvent(final RabbitPushEvent event, final Message message) throws InterruptedException {

        MessageProperties properties = message.getMessageProperties();
        // set context
        ServiceContext serviceContext = new ServiceContext(properties.getMessageId(), 
                                                           properties.getMessageId(),
                                                           Constants.REQUEST_ORIGIN_RABBITMQ,
                                                           Constants.REQUEST_ORIGIN_RABBITMQ,
                                                           null,
                                                           Constants.REQUEST_ORIGIN_INTERNAL,
                                                           null,
                                                           null,
                                                           null);

        logger.info("event:" + properties.getReceivedRoutingKey() + " message_id:" + properties.getMessageId()
            + " correlation_id:" + properties.getCorrelationId() + " redelivered:" + properties.getRedelivered());

        try {
            pushNotificationService.sendPushNotification(serviceContext,
                                                         event.getNotificationType(),
                                                         event.getExternalEntity(),
                                                         event.getPushNotification());
        } catch (TemplateValidationException e) {
            logger.error("message_id:" + properties.getMessageId() + " error:" + e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
        } catch (ExternalCommunicationException e) {
            logger.error("message_id:" + properties.getMessageId() + " error:" + e.getMessage());
            throw new ListenerExecutionFailedException(e.getMessage(), e, message);
        }
    }

}