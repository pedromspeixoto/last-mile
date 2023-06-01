package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayOutPaymentType {

    normal, instant;

    public String getEasypayOutPaymentType() {
        return name();
    }
}