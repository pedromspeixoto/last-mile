package com.lastmile.accountservice.dto;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.notifications.NotificationTypes;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class CreatePushNotificationRequestDto {

    @NotNull(message = "Notification type is mandatory")
    private NotificationTypes notificationType;

    @NotBlank(message = "Notification title is mandatory")
    private String notificationTitle;

    @NotBlank(message = "Notification text is mandatory")
    private String notificationText;

    private String notificationName;

    private String notificationImageUrl;

    private Map<String, String> data;

    public NotificationTypes getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(NotificationTypes notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationTitle() {
        return this.notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return this.notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getNotificationName() {
        return this.notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public String getNotificationImageUrl() {
        return this.notificationImageUrl;
    }

    public void setNotificationImageUrl(String notificationImageUrl) {
        this.notificationImageUrl = notificationImageUrl;
    }

    public Map<String,String> getData() {
        return this.data;
    }

    public void setData(Map<String,String> data) {
        this.data = data;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}