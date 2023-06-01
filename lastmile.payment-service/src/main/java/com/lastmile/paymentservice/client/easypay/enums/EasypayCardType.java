package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayCardType {

    VISA, MasterCard;

    public String getEasypayCallbackType() {
        return name();
    }
}