package com.lastmile.accountservice.client.rabbitmq;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.lastmile.utils.models.rabbitmq.push.PushNotification;
import com.lastmile.utils.models.rabbitmq.push.RabbitPushEvent;
import com.lastmile.utils.models.rabbitmq.sms.RabbitSmsEvent;
import com.lastmile.utils.models.rabbitmq.sms.RabbitSmsModel;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.notifications.NotificationTypes;
import com.lastmile.utils.enums.notifications.PushNotificationsExternalEntities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Configuration
@Component
public class EventPublisher {

    @Value("${rabbitmq.routing-key.account-push-notification}")
    private String accountPushNotificationRoutingKey;

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    public static final String INVALID_USER_PHONE_NUMBER = "phoneNumber %s is not valid!";

    @Autowired
    private AmqpTemplate communicationExchangeConnector;

    // send sms event to rabbitmq broker
    public void sendSmsMessage(ServiceContext context, String routingKey, String template, String to,
        Map<String, Object> model) throws NumberParseException {

        RabbitSmsEvent object = new RabbitSmsEvent();
        RabbitSmsModel sms = new RabbitSmsModel();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        PhoneNumber phone;

        try {
            // parse phone number
            phone = phoneUtil.parse(to, "");
        } catch (NumberParseException e) {
            logger.error(String.format(INVALID_USER_PHONE_NUMBER, to), e);
            throw e;
        }

        String countryCode = String.valueOf(phone.getCountryCode());
        String number = String.valueOf(phone.getNationalNumber());
        
        sms.setCountryCode("+" + countryCode);
        sms.setPhoneNumber(number);
        sms.setProperties(model);

        object.setTemplate(template);
        object.setTextMessage(sms);

        try {
            communicationExchangeConnector.convertAndSend(routingKey, object, m -> {
                m.getMessageProperties().setCorrelationId(context.getCorrelationId());
                m.getMessageProperties().setHeader("user_id", context.getUserId());
                m.getMessageProperties().setHeader("permissions", context.getPermissions());
                return m;
            });
        } catch (AmqpException e) {
            throw e;
        }
    }

    // send sms event to rabbitmq broker
    public void sendPushNotification(ServiceContext context, NotificationTypes notificationType, PushNotificationsExternalEntities externalEntity,
                                     String userIdentification, String deviceToken, String notificationTitle, String notificationText,
                                     String notificationName, String notificationImageUrl, Map<String,String> notificationData) {

        RabbitPushEvent event = new RabbitPushEvent();
        PushNotification pushNotification = new PushNotification();

        pushNotification.setTo(userIdentification);
        pushNotification.setDeviceToken(deviceToken);
        pushNotification.setNotificationTitle(notificationTitle);
        pushNotification.setNotificationText(notificationText);
        pushNotification.setNotificationName(notificationName);
        pushNotification.setNotificationImageUrl(notificationImageUrl);
        pushNotification.setData(notificationData);

        event.setExternalEntity(externalEntity.toString());
        event.setNotificationType(notificationType.toString());
        event.setPushNotification(pushNotification);

        try {
            communicationExchangeConnector.convertAndSend(accountPushNotificationRoutingKey, event, m -> {
                m.getMessageProperties().setCorrelationId(context.getCorrelationId());
                m.getMessageProperties().setHeader("user_id", context.getUserId());
                m.getMessageProperties().setHeader("permissions", context.getPermissions());
                return m;
            });
        } catch (AmqpException e) {
            throw e;
        }

    }

}