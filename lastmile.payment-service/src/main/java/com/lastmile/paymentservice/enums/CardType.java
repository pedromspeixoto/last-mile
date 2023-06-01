package com.lastmile.paymentservice.enums;

public enum CardType {

    VISA, MASTERCARD;

    public String getCardType() {
        return name();
    }
}