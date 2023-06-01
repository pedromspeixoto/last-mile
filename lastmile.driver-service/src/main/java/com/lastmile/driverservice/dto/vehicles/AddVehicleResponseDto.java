package com.lastmile.driverservice.dto.vehicles;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AddVehicleResponseDto {

    private String vehicleIdentification;

    public AddVehicleResponseDto() {
    }

    public AddVehicleResponseDto(String vehicleIdentification) {
        this.vehicleIdentification = vehicleIdentification;
    }

    public String getVehicleIdentification() {
        return this.vehicleIdentification;
    }

    public void setVehicleIdentification(String vehicleIdentification) {
        this.vehicleIdentification = vehicleIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}