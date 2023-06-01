package com.lastmile.paymentservice.enums;

public enum OutPaymentStatus {

    PENDING, SCHEDULED, CREATED, PAID, FAILED, CANCELLED;

    public String getOutPaymentStatus() {
        return name();
    }
}