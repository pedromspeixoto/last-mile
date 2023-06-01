package com.lastmile.orderengine.enums;

public enum OrderPriority {

    LOW, MEDIUM, HIGH;

    public String getOrderPriority() {
        return name();
    }
}