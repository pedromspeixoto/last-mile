package com.lastmile.paymentservice.enums;

public enum PaymentType {

    DIRECT, DEFERRED;

    public String getPaymentType() {
        return name();
    }
}