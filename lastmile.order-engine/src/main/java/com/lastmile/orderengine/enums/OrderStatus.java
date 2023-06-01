package com.lastmile.orderengine.enums;

public enum OrderStatus {

    PENDING, ACCEPTED, PICKED_UP, IN_TRANSIT, FINALIZED;

    public String getOrderStatus() {
        return name();
    }
}