package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@IdClass(VehicleID.class)
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @Column(name = "make")
    private String make;

    @Id
    @Column(name = "model")
    private String model;

    @Id
    @Column(name = "category")
    private String category;

    @Id
    @Column(name = "year")
    private int year;

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

}