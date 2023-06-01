package com.lastmile.driverservice.dto.vehicles;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ValidateVehicleRequestDto {

    private Boolean validated;

    public Boolean isValidated() {
        return this.validated;
    }

    public Boolean getValidated() {
        return this.validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}