package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayOutResponseStatus {

    pending, delayed, success, deleted;

    public String getEasypayOutResponseStatus() {
        return name();
    }
}