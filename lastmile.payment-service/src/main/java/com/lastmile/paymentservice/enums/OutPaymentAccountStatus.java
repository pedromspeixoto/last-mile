package com.lastmile.paymentservice.enums;

public enum OutPaymentAccountStatus {

    ACTIVE, INACTIVE;

    public String getOutPaymentAccountStatus() {
        return name();
    }
}