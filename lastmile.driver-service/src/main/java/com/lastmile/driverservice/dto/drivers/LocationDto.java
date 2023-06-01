package com.lastmile.driverservice.dto.drivers;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class LocationDto {

    private Double latitude;

    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}