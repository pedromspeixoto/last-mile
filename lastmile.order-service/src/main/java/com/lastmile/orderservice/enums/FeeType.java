package com.lastmile.orderservice.enums;

public enum FeeType {

    FIXED, MULTIPLIER, EQUALS, SURGE;

    public String getFeeType() {
        return name();
    }
}