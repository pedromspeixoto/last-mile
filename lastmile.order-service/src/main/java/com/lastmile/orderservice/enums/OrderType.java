package com.lastmile.orderservice.enums;

public enum OrderType {

    NORMAL, FRAGILE;

    public String getOrderType() {
        return name();
    }
}