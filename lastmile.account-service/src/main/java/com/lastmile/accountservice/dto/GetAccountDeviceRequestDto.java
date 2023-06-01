package com.lastmile.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.notifications.PushNotificationsExternalEntities;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class GetAccountDeviceRequestDto {

    private PushNotificationsExternalEntities externalEntity;

    private String externalEntityToken;

    public PushNotificationsExternalEntities getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(PushNotificationsExternalEntities externalEntity) {
        this.externalEntity = externalEntity;
    }

    public String getExternalEntityToken() {
        return this.externalEntityToken;
    }

    public void setExternalEntityToken(String externalEntityToken) {
        this.externalEntityToken = externalEntityToken;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}