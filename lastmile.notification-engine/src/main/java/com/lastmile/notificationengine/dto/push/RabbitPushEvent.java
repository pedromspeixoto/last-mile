package com.lastmile.notificationengine.dto.push;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lastmile.notificationengine.domain.push.PushNotification;

public class RabbitPushEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "notification_type", required = true)
    private String notificationType;

    @JsonProperty(value = "external_entity", required = true)
    private String externalEntity;

    @JsonProperty(value = "notification", required = true)
    private PushNotification pushNotification;

    public RabbitPushEvent(String notificationType, String externalEntity, PushNotification pushNotification) {
	    super();
        this.notificationType = notificationType;
        this.externalEntity = externalEntity;
	    this.pushNotification = pushNotification;
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public PushNotification getPushNotification() {
        return this.pushNotification;
    }

    public void setPushNotification(PushNotification pushNotification) {
        this.pushNotification = pushNotification;
    }

    public String getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(String externalEntity) {
        this.externalEntity = externalEntity;
    }

}