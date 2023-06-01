package com.lastmile.driverservice.enums;

public enum PaymentFrequency {

    DAILY, MONTHLY;

    public String getPaymentFrequency() {
        return name();
    }
}