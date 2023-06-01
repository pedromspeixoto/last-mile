package com.lastmile.orderservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class EstimateResponseDto {

    private Integer pickupEta;

    private Integer deliveryEta;

    private Integer estimatedPickupDistance;

    private Integer estimatedDeliveryDistance;

    private Double estimatedDeliveryFee;

    public Integer getPickupEta() {
        return this.pickupEta;
    }

    public void setPickupEta(Integer pickupEta) {
        this.pickupEta = pickupEta;
    }

    public Integer getDeliveryEta() {
        return this.deliveryEta;
    }

    public void setDeliveryEta(Integer deliveryEta) {
        this.deliveryEta = deliveryEta;
    }

    public Double getEstimatedDeliveryFee() {
        return this.estimatedDeliveryFee;
    }

    public void setEstimatedDeliveryFee(Double estimatedDeliveryFee) {
        this.estimatedDeliveryFee = estimatedDeliveryFee;
    }

    public Integer getEstimatedPickupDistance() {
        return this.estimatedPickupDistance;
    }

    public void setEstimatedPickupDistance(Integer estimatedPickupDistance) {
        this.estimatedPickupDistance = estimatedPickupDistance;
    }

    public Integer getEstimatedDeliveryDistance() {
        return this.estimatedDeliveryDistance;
    }

    public void setEstimatedDeliveryDistance(Integer estimatedDeliveryDistance) {
        this.estimatedDeliveryDistance = estimatedDeliveryDistance;
    }

    public EstimateResponseDto() {
    }

    public EstimateResponseDto(Integer pickupEta,
                               Integer deliveryEta,
                               Integer estimatedPickupDistance,
                               Integer estimatedDeliveryDistance,
                               Double estimatedDeliveryFee) {
        this.pickupEta = pickupEta;
        this.deliveryEta = deliveryEta;
        this.estimatedPickupDistance = estimatedPickupDistance;
        this.estimatedDeliveryDistance = estimatedDeliveryDistance;
        this.estimatedDeliveryFee = estimatedDeliveryFee;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}