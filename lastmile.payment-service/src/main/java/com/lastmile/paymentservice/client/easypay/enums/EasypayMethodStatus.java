package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayMethodStatus {

    waiting, pending, active, deleted;

    public String getEasypayMethodStatus() {
        return name();
    }

}