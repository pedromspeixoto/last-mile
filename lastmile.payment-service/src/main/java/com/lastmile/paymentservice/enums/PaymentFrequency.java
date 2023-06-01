package com.lastmile.paymentservice.enums;

public enum PaymentFrequency {

    DAILY, MONTHLY;

    public String getPaymentFrequency() {
        return name();
    }
}