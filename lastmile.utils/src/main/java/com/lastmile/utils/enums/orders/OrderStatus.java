package com.lastmile.utils.enums.orders;

public enum OrderStatus {

    PENDING, SCHEDULED, PUBLISHED, FAILED_ON_CREATE, PAYMENT_FAILED, ESTIMATE_FAILED, ASSIGNED, ACCEPTED, IN_TRANSIT, FINALIZED, REJECTED;

    public String getOrderStatus() {
        return name();
    }
}