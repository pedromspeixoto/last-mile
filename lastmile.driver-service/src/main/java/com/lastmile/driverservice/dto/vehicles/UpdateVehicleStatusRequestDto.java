package com.lastmile.driverservice.dto.vehicles;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class UpdateVehicleStatusRequestDto {

    private Boolean vehicleActive;

    public Boolean getVehicleActive() {
        return this.vehicleActive;
    }

    public Boolean isVehicleActive() {
        return this.vehicleActive;
    }

    public void setVehicleActive(Boolean vehicleActive) {
        this.vehicleActive = vehicleActive;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}