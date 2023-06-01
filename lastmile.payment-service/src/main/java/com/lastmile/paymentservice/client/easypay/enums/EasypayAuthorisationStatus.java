package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayAuthorisationStatus {

    pending, failed, success;

    public String getEasypayAuthorisationStatus() {
        return name();
    }
}