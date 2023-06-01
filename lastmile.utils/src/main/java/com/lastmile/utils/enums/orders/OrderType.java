package com.lastmile.utils.enums.orders;

public enum OrderType {

    NORMAL, FRAGILE;

    public String getOrderType() {
        return name();
    }
}