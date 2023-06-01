package com.lastmile.driverservice.dto.vehicles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.enums.DriverVehicleType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class AddVehicleRequestDto {

    private String make;

    private String model;

    private String category;

    private Integer year;

    private DriverVehicleType vehicleType;

    private String licensePlate;

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

    public DriverVehicleType getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(DriverVehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return this.licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}