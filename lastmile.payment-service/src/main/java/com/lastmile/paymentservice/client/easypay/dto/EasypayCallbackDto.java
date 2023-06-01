package com.lastmile.paymentservice.client.easypay.dto;

import java.util.List;

import com.lastmile.paymentservice.client.easypay.enums.EasypayCallbackStatus;
import com.lastmile.paymentservice.client.easypay.enums.EasypayCallbackType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class EasypayCallbackDto {

    private String id;

    private String key;

    private EasypayCallbackType type;

    private EasypayCallbackStatus status;

    private List<String> messages;

    private String date;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EasypayCallbackType getType() {
        return this.type;
    }

    public void setType(EasypayCallbackType type) {
        this.type = type;
    }

    public EasypayCallbackStatus getStatus() {
        return this.status;
    }

    public void setStatus(EasypayCallbackStatus status) {
        this.status = status;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}