package com.lastmile.driverservice.domain;

import java.io.Serializable;

public class VehicleID implements Serializable {

	private static final long serialVersionUID = 1L;

	private String make;

    private String model;

    private String category;

    private int year;

    public VehicleID() {
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

    public VehicleID(String make, String model, String category, int year) {
        this.make = make;
        this.model = model;
        this.category = category;
        this.year = year;
    }

}