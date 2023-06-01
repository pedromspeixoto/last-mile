package com.lastmile.utils.enums.payments;

public enum PaymentDetailType {

    MBWAY, CREDITCARD, DEBIT;

    public String getPaymentDetailType() {
        return name();
    }
}