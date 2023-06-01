package com.lastmile.driverservice.enums;

public enum DriverStatus {

    PENDING, OFFLINE, AVAILABLE, IN_TRANSIT, INACTIVE;

    public String getDriverStatus() {
        return name();
    }
}