package com.lastmile.paymentservice.enums;

public enum PaymentDetailStatus {

    PENDING, CREATED, ACCEPTED, FAILED;

    public String getPaymentDetailStatus() {
        return name();
    }
}