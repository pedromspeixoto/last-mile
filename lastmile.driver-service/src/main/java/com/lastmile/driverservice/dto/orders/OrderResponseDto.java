package com.lastmile.driverservice.dto.orders;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderResponseDto {

    private String orderIdentification;

    private String externalOrderIdentification;

    private String requesterName;

    private String requesterCountry;

    private String requesterCity;

    private String requesterZipCode;

    private Double pickupLatitude;

    private Double pickupLongitude;

    private String destinationCountry;

    private String destinationCity;

    private String destinationZipCode;

    private Double destinationLatitude;

    private Double destinationLongitude;

    private String status;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private Date scheduledDate;

    private String orderType;

    private String priority;

    private String assignedDriver;

    private Integer packagingEta;

    private Integer pickupEta;

    private Integer deliveryEta;

    private Integer effectivePickupTime;

    private Integer effectiveDeliveryTime;

    public String getOrderIdentification() {
        return this.orderIdentification;
    }

    public void setOrderIdentification(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    public String getExternalOrderIdentification() {
        return this.externalOrderIdentification;
    }

    public void setExternalOrderIdentification(String externalOrderIdentification) {
        this.externalOrderIdentification = externalOrderIdentification;
    }

    public String getRequesterName() {
        return this.requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterCountry() {
        return this.requesterCountry;
    }

    public void setRequesterCountry(String requesterCountry) {
        this.requesterCountry = requesterCountry;
    }

    public String getRequesterCity() {
        return this.requesterCity;
    }

    public void setRequesterCity(String requesterCity) {
        this.requesterCity = requesterCity;
    }

    public String getRequesterZipCode() {
        return this.requesterZipCode;
    }

    public void setRequesterZipCode(String requesterZipCode) {
        this.requesterZipCode = requesterZipCode;
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

    public String getDestinationCountry() {
        return this.destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDestinationCity() {
        return this.destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationZipCode() {
        return this.destinationZipCode;
    }

    public void setDestinationZipCode(String destinationZipCode) {
        this.destinationZipCode = destinationZipCode;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getOrderType() {
        return this.orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedDriver() {
        return this.assignedDriver;
    }

    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Integer getPackagingEta() {
        return this.packagingEta;
    }

    public void setPackagingEta(Integer packagingEta) {
        this.packagingEta = packagingEta;
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

    public Integer getEffectivePickupTime() {
        return this.effectivePickupTime;
    }

    public void setEffectivePickupTime(Integer effectivePickupTime) {
        this.effectivePickupTime = effectivePickupTime;
    }

    public Integer getEffectiveDeliveryTime() {
        return this.effectiveDeliveryTime;
    }

    public void setEffectiveDeliveryTime(Integer effectiveDeliveryTime) {
        this.effectiveDeliveryTime = effectiveDeliveryTime;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}