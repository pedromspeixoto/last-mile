package com.lastmile.paymentservice.client.easypay.enums;

public enum EasypayMethod {

    mb, cc, dd, mbw;

    public String getEasypayMethod() {
        return name();
    }

}