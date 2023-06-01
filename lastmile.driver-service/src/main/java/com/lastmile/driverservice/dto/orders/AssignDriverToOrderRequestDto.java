package com.lastmile.driverservice.dto.orders;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AssignDriverToOrderRequestDto {
        
    private String driverIdentification;

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}