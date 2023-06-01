package com.lastmile.orderservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderservice.enums.OrderPriority;
import com.lastmile.orderservice.enums.OrderStatus;
import com.lastmile.orderservice.enums.OrderType;
import com.lastmile.utils.validations.ValidCountryCode;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderUpdateRequestDto {

	private String requesterCity;

    private String requesterZipCode;

    private Double pickupLatitude;

    private Double pickupLongitude;

    @ValidCountryCode
    private String destinationCountry;

    private String destinationCity;

    private String destinationZipCode;

    private Double destinationLatitude;

    private Double destinationLongitude;

    private OrderPriority priority;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private Date scheduledDate;

    private OrderStatus orderStatus;

    private OrderType orderType;

    private String assignedDriver;

    private Integer deliveryEta;

    private Boolean isOrderTrackActive;

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

    public OrderPriority getPriority() {
        return this.priority;
    }

    public void setPriority(OrderPriority priority) {
        this.priority = priority;
    }

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getAssignedDriver() {
        return this.assignedDriver;
    }

    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Integer getDeliveryEta() {
        return this.deliveryEta;
    }

    public void setDeliveryEta(Integer deliveryEta) {
        this.deliveryEta = deliveryEta;
    }

    public Boolean isIsOrderTrackActive() {
        return this.isOrderTrackActive;
    }

    public Boolean getIsOrderTrackActive() {
        return this.isOrderTrackActive;
    }

    public void setIsOrderTrackActive(Boolean isOrderTrackActive) {
        this.isOrderTrackActive = isOrderTrackActive;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}