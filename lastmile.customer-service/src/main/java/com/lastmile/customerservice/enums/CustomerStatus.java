package com.lastmile.customerservice.enums;

public enum CustomerStatus {

    PENDING, ACTIVE, INACTIVE;

    public String getCustomerStatus() {
        return name();
    }
}