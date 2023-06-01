package com.lastmile.notificationengine.client.firebase;

import java.util.Map;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.lastmile.notificationengine.domain.push.PushNotification;
import com.lastmile.utils.exceptions.GenericException;
import com.lastmile.utils.logs.CustomLogging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseClient {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private CustomLogging logger;

    public void sendSinglePushNotification(PushNotification pushNotification) throws GenericException {
        String token = pushNotification.getDeviceToken();

        // set notification
        Notification notification;
        if (null != pushNotification.getNotificationImageUrl() && !pushNotification.getNotificationImageUrl().isEmpty()) {
            notification = createNotificationWithImage(pushNotification);
        } else {
            notification = createNotificationWithoutImage(pushNotification);
        }

        // set message
        Message message;
        if (null != pushNotification.getData() && !pushNotification.getData().isEmpty()) {
            message = createMessageWithData(token,
                                            notification,
                                            pushNotification.getData());
        } else {
            message = createMessageWithoutData(token,
                                               notification);
        }

        // send message
        String response = "";
        try {
            response = firebaseMessaging.send(message);
        } catch (FirebaseMessagingException ex) {
            throw new GenericException("exception sending push notification using firebase"
                                       + "; error_code: " + ex.getErrorCode().toString()
                                       + "; error_message: " + ex.getMessage()
                                       + "; messaging_error_code: " + ex.getMessagingErrorCode().toString(), ex.getCause());
        }
        logger.info("successfully sent singe push notification using firebase: " + response);
    }

    private Notification createNotificationWithImage(PushNotification pushNotification) {
        return Notification.builder()
                           .setTitle(pushNotification.getNotificationTitle())
                           .setBody(pushNotification.getNotificationText())
                           .setImage("test")
                           .build();
    }

    private Notification createNotificationWithoutImage(PushNotification pushNotification) {
        return Notification.builder()
                           .setTitle(pushNotification.getNotificationTitle())
                           .setBody(pushNotification.getNotificationText())
                           .build();
    }

    private Message createMessageWithoutData(String token, Notification notification) {
        return Message.builder()
                      .setToken(token)
                      .setNotification(notification)
                      .build();
    }

    private Message createMessageWithData(String token, Notification notification, Map<String,String> data) {
        return Message.builder()
                      .setToken(token)
                      .setNotification(notification)
                      .putAllData(data)
                      .build();
    }

}