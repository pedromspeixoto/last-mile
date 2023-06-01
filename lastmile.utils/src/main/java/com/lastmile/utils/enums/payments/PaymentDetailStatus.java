package com.lastmile.utils.enums.payments;

public enum PaymentDetailStatus {

    PENDING, CREATED, ACCEPTED, FAILED;

    public String getPaymentDetailStatus() {
        return name();
    }
}