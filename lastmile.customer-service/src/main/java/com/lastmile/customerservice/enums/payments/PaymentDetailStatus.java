package com.lastmile.customerservice.enums.payments;

public enum PaymentDetailStatus {

    PENDING, CREATED, ACCEPTED, FAILED;

    public String getPaymentDetailStatus() {
        return name();
    }
}