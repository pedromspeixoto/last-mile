package com.lastmile.utils.enums.orders;

public enum FeeType {

    FIXED, MULTIPLIER, EQUALS;

    public String getFeeType() {
        return name();
    }
}