package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayType {

    sale, authorization;

    public String getEasypayType() {
        return name();
    }
}