package com.lastmile.orderservice.dto.drivers;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DriverResponseModel {

    private String driverIdentification;

    private GetAccountDto profile;

    private Double latitude;

    private Double longitude;

    private DriverVehicleResponseDto activeVehicle;

    private Integer driverRating;

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
    }

    public GetAccountDto getProfile() {
        return this.profile;
    }

    public void setProfile(GetAccountDto profile) {
        this.profile = profile;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public DriverVehicleResponseDto getActiveVehicle() {
        return this.activeVehicle;
    }

    public void setActiveVehicle(DriverVehicleResponseDto activeVehicle) {
        this.activeVehicle = activeVehicle;
    }

    public Integer getDriverRating() {
        return this.driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}