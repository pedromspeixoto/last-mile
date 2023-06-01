package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayCallbackType {

    authorisation, capture, frequent_create, refund,chargeback, subscription_create, subscription_capture, out_payment;

    public String getEasypayCallbackType() {
        return name();
    }
}