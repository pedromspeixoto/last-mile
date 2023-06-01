package com.lastmile.paymentservice.enums;

public enum PaymentStatus {

    PENDING, CREATED, DEFERRED, ACCEPTED, DENIED;

    public String getPaymentStatus() {
        return name();
    }
}