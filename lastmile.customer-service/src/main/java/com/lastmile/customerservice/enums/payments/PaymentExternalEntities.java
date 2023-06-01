package com.lastmile.customerservice.enums.payments;

public enum PaymentExternalEntities {

    EASYPAY, NONE;

    public String getPaymentExternalEntities() {
        return name();
    }
}