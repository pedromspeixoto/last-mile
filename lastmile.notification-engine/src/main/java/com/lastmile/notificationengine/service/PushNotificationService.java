package com.lastmile.notificationengine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lastmile.notificationengine.client.firebase.FirebaseClient;
import com.lastmile.notificationengine.domain.push.PushNotification;
import com.lastmile.notificationengine.enums.push.NotificationTypes;
import com.lastmile.notificationengine.enums.push.PushNotificationsExternalEntities;
import com.lastmile.notificationengine.service.exception.ExternalCommunicationException;
import com.lastmile.notificationengine.service.exception.TemplateValidationException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;

@Component
public class PushNotificationService {

    @Autowired
    private FirebaseClient firebaseClient;

    @Autowired
    private CustomLogging logger;

    public void sendPushNotification(ServiceContext serviceContext,
                                     String notificationType,
                                     String externalEntity,
                                     PushNotification pushNotification) throws TemplateValidationException, ExternalCommunicationException {

        // validate request
        NotificationTypes notificationTypeEnum;
        try {
            notificationTypeEnum = NotificationTypes.valueOf(notificationType);
        } catch (IllegalArgumentException ex) {  
            throw new TemplateValidationException("invalid notification type: " + notificationType);
        }
        PushNotificationsExternalEntities externalEntityEnum;
        try {
            externalEntityEnum = PushNotificationsExternalEntities.valueOf(externalEntity);
        } catch (IllegalArgumentException ex) {  
            throw new TemplateValidationException("invalid external entity: " + externalEntity);
        }

        switch (externalEntityEnum) {
            case FIREBASE:
                switch (notificationTypeEnum) {
                    case SINGLE:
                        // validate device token if single notification
                        if (null == pushNotification.getDeviceToken() || pushNotification.getDeviceToken().isEmpty()) {
                            throw new TemplateValidationException("device token is mandatory for firebase single push notificaiton");
                        }
                        try {
                            firebaseClient.sendSinglePushNotification(pushNotification);
                        } catch (Exception e) {
                            throw new ExternalCommunicationException(e.getMessage());
                        }
                        logger.info("single push notification sent to user" + pushNotification.getTo());
                        break;
                    default:
                        throw new TemplateValidationException("invalid notification type for firebase: " + notificationType);
                }
                break;
        }

    }

}