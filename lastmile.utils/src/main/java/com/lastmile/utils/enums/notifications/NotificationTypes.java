package com.lastmile.utils.enums.notifications;

public enum NotificationTypes {

    SINGLE, TOPIC;

    public String getNotificationType() {
        return name();
    }
}