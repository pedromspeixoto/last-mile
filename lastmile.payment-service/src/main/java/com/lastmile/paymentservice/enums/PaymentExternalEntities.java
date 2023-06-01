package com.lastmile.paymentservice.enums;

public enum PaymentExternalEntities {

    EASYPAY, NONE;

    public String getPaymentExternalEntities() {
        return name();
    }
}