package com.lastmile.utils.enums.payments;

public enum PaymentType {

    DIRECT, DEFERRED;

    public String getPaymentType() {
        return name();
    }
}