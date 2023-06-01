package com.lastmile.driverservice.dto.vehicles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.dto.documents.VehicleRegistrationResponseDto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class DriverVehicleResponseDto {

    private String vehicleIdentification;

    private String make;

    private String model;

    private String category;

    private Integer year;

    private String vehicleType;

    private String licensePlate;

    private VehicleRegistrationResponseDto vehicleRegistration;

    private Boolean vehicleActive;

    private Boolean entityValidated;

    public Boolean isVehicleActive() {
        return this.vehicleActive;
    }

    public Boolean getVehicleActive() {
        return this.vehicleActive;
    }

    public void setVehicleActive(Boolean vehicleActive) {
        this.vehicleActive = vehicleActive;
    }

    public String getMake() {
        return this.make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getVehicleIdentification() {
        return this.vehicleIdentification;
    }

    public void setVehicleIdentification(String vehicleIdentification) {
        this.vehicleIdentification = vehicleIdentification;
    }


    public String getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return this.licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleRegistrationResponseDto getVehicleRegistration() {
        return this.vehicleRegistration;
    }

    public void setVehicleRegistration(VehicleRegistrationResponseDto vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    public Boolean isEntityValidated() {
        return this.entityValidated;
    }

    public Boolean getEntityValidated() {
        return this.entityValidated;
    }

    public void setEntityValidated(Boolean entityValidated) {
        this.entityValidated = entityValidated;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}