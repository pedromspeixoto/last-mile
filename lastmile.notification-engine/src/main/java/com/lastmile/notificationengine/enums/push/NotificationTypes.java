package com.lastmile.notificationengine.enums.push;

public enum NotificationTypes {

    SINGLE, TOPIC;

    public String getNotificationType() {
        return name();
    }
}