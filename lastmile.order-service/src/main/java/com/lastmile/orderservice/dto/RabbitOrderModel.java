package com.lastmile.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class RabbitOrderModel {

    @JsonProperty("orderIdentification")
    private String orderIdentification;

    @JsonProperty("pickup_latitude")
    private Double pickupLatitude;

    @JsonProperty("pickup_longitude")
    private Double pickupLongitude;

    @JsonProperty("destination_latitude")
    private Double destinationLatitude;

    @JsonProperty("destination_longitude")
    private Double destinationLongitude;

    @JsonProperty("order_value")
    private Double orderValue;

    public String getOrderIdentification() {
        return this.orderIdentification;
    }

    public void setOrderIdentification(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    @JsonProperty("pickup_latitude")
    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    @JsonProperty("pickup_latitude")
    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    @JsonProperty("pickup_longitude")
    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    @JsonProperty("pickup_longitude")
    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    @JsonProperty("destination_latitude")
    public Double getDestinationLatitude() {
        return destinationLatitude;
    }
    
    @JsonProperty("destination_latitude")
    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }
    
    @JsonProperty("destination_longitude")
    public Double getDestinationLongitude() {
        return destinationLongitude;
    }
    
    @JsonProperty("destination_longitude")
    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    @JsonProperty("order_value")
    public Double getOrderValue() {
        return orderValue;
    }

    @JsonProperty("order_value")
    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}