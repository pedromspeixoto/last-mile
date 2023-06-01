package com.lastmile.driverservice.enums;

public enum FiscalEntityStatus {

    PENDING, ACTIVE, INACTIVE;

    public String getFiscalEntityStatus() {
        return name();
    }
}