package com.lastmile.utils.enums.payments;

public enum OutPaymentStatus {

    PENDING, SCHEDULED, PAID, FAILED, CANCELLED;

    public String getOutPaymentStatus() {
        return name();
    }
}