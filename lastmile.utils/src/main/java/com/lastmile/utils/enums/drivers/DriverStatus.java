package com.lastmile.utils.enums.drivers;

public enum DriverStatus {

    PENDING, PENDING_VALIDATION, VALIDATED, PENDING_ACTION, IN_TRANSIT, AVAILABLE, BUSY, INACTIVE;

    public String getDriverStatus() {
        return name();
    }
}