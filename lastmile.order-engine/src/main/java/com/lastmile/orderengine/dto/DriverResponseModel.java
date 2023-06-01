package com.lastmile.orderengine.dto;

public class DriverResponseModel {

    private String driverIdentification;

    private String country;

    private String city;

    private String zipCode;

    private float latitude;

    private float longitude;

    private String status;

    private int driverRating;

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }


    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDriverRating() {
        return this.driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

}