package com.lastmile.orderservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderservice.enums.OrderStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderFollowResponseDto {

    private String orderIdentification;

    private String shortOrderIdentification;

    private String ownerIdentification;

    private String requesterPhoneNumber;

    private String driverName;

    private Integer driverRating;

    private String driverVehicleLicensePlate;

    private String driverVehicleDescription;

    private byte[] driverPhoto;

    private Double currentLatitude;

    private Double currentLongitude;

    private Double pickupLatitude;

    private Double pickupLongitude;

    private String pickupAddress;

    private Double destinationLatitude;

    private Double destinationLongitude;

    private String destinationAddress;

    private Integer pickupEta;

    private Integer deliveryEta;

    private OrderStatus status;

    private Date scheduledDate;

    public String getOrderIdentification() {
        return this.orderIdentification;
    }

    public void setOrderIdentification(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    public String getOwnerIdentification() {
        return this.ownerIdentification;
    }

    public void setOwnerIdentification(String ownerIdentification) {
        this.ownerIdentification = ownerIdentification;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Integer getDriverRating() {
        return this.driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public String getDriverVehicleLicensePlate() {
        return this.driverVehicleLicensePlate;
    }

    public void setDriverVehicleLicensePlate(String driverVehicleLicensePlate) {
        this.driverVehicleLicensePlate = driverVehicleLicensePlate;
    }

    public String getDriverVehicleDescription() {
        return this.driverVehicleDescription;
    }

    public void setDriverVehicleDescription(String driverVehicleDescription) {
        this.driverVehicleDescription = driverVehicleDescription;
    }

    public byte[] getDriverPhoto() {
        return this.driverPhoto;
    }

    public void setDriverPhoto(byte[] driverPhoto) {
        this.driverPhoto = driverPhoto;
    }

    public Double getCurrentLatitude() {
        return this.currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return this.currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Double getPickupLatitude() {
        return this.pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public Double getPickupLongitude() {
        return this.pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public Double getDestinationLatitude() {
        return this.destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return this.destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

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

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getPickupAddress() {
        return this.pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDestinationAddress() {
        return this.destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getShortOrderIdentification() {
        return this.shortOrderIdentification;
    }

    public void setShortOrderIdentification(String shortOrderIdentification) {
        this.shortOrderIdentification = shortOrderIdentification;
    }

    public String getRequesterPhoneNumber() {
        return this.requesterPhoneNumber;
    }

    public void setRequesterPhoneNumber(String requesterPhoneNumber) {
        this.requesterPhoneNumber = requesterPhoneNumber;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}