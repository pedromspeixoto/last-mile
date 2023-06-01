package com.lastmile.driverservice.enums;

public enum DriverVehicleType {

    CAR, MOTOCYCLE, TRUCK, BIKE;

    public String getDriverVehicleType() {
        return name();
    }
}