package com.lastmile.paymentservice.enums;

public enum PaymentDetailType {

    MBWAY, CREDITCARD, DEBIT;

    public String getPaymentDetailType() {
        return name();
    }
}