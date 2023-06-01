package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "drivers_vehicles")
public class DriverVehicle extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_identification")
    private String vehicleIdentification;

    @Column(name = "driver_identification")
    private String driverIdentification;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "category")
    private String category;

    @Column(name = "year")
    private int year;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "vehicle_active")
    private boolean vehicleActive;

    @Column(name = "entity_validated")
    private boolean entityValidated;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleIdentification() {
        return this.vehicleIdentification;
    }

    public void setVehicleIdentification(String vehicleIdentification) {
        this.vehicleIdentification = vehicleIdentification;
    }

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
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

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public boolean isVehicleActive() {
        return this.vehicleActive;
    }

    public boolean getVehicleActive() {
        return this.vehicleActive;
    }

    public void setVehicleActive(boolean vehicleActive) {
        this.vehicleActive = vehicleActive;
    }

    public boolean isEntityValidated() {
        return this.entityValidated;
    }

    public boolean getEntityValidated() {
        return this.entityValidated;
    }

    public void setEntityValidated(boolean entityValidated) {
        this.entityValidated = entityValidated;
    }

}