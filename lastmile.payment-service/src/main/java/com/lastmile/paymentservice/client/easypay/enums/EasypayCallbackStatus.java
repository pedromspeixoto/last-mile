package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayCallbackStatus {

    failed, success;

    public String getEasypayCallbackStatus() {
        return name();
    }
}