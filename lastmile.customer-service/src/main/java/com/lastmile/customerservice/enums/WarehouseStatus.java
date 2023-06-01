package com.lastmile.customerservice.enums;

public enum WarehouseStatus {

    ACTIVE, INACTIVE;

    public String getWarehouseStatus() {
        return name();
    }
}