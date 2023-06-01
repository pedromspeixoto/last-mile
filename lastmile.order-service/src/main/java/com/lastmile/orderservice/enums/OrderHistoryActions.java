package com.lastmile.orderservice.enums;

public enum OrderHistoryActions {

    CREATED, ACCEPTED, REJECTED, ASSIGNED, PICKED_UP, FINALIZED, CANCELLED;

    public String getOrderStatus() {
        return name();
    }
}