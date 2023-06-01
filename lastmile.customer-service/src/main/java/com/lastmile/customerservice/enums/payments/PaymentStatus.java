package com.lastmile.customerservice.enums.payments;

public enum PaymentStatus {

    PENDING, CREATED, DEFERRED, ACCEPTED, DENIED;

    public String getPaymentStatus() {
        return name();
    }
}