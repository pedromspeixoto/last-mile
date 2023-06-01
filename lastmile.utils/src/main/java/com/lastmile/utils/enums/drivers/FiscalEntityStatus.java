package com.lastmile.utils.enums.drivers;

public enum FiscalEntityStatus {

    PENDING, ACTIVE, INACTIVE;

    public String getFiscalEntityStatus() {
        return name();
    }
}