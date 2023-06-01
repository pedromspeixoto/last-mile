package com.lastmile.orderservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.payments.PaymentStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderResponseDto {

    private String orderIdentification;

    private String shortOrderIdentification;

    private String externalOrderIdentification;

    private String requesterIdentification;

    private String requesterEntityName;

    private String requesterEntityType;

    private String requesterName;

    private String requesterPhoneNumber;

    private String requesterEmail;

    private String requesterCountry;

    private String requesterCity;

    private String requesterZipCode;

    private Double pickupLatitude;

    private Double pickupLongitude;

    private String pickupAddress;

    private String destinationCountry;

    private String destinationCity;

    private String destinationZipCode;

    private Double destinationLatitude;

    private Double destinationLongitude;

    private String destinationAddress;

    private String status;

    private String orderType;

    private String priority;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private Date scheduledDate;

    private String assignedDriver;

    private Integer packagingEta;

    private Integer pickupEta;

    private Integer deliveryEta;

    private Integer effectivePickupTime;

    private Integer effectiveDeliveryTime;

    private Double orderValue;

    private Integer orderRating;

    private String ownerIdentification;

    private String paymentDetailsId;

    private PaymentStatus paymentStatus;

    private Boolean isOrderTrackingActive;

    private Date createdDate;

    private OrderShortHistoryResponseDto history;

    public String getOrderIdentification() {
        return this.orderIdentification;
    }

    public void setOrderIdentification(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    public String getShortOrderIdentification() {
        return this.shortOrderIdentification;
    }

    public void setShortOrderIdentification(String shortOrderIdentification) {
        this.shortOrderIdentification = shortOrderIdentification;
    }

    public String getExternalOrderIdentification() {
        return this.externalOrderIdentification;
    }

    public void setExternalOrderIdentification(String externalOrderIdentification) {
        this.externalOrderIdentification = externalOrderIdentification;
    }

    public String getRequesterIdentification() {
        return this.requesterIdentification;
    }

    public void setRequesterIdentification(String requesterIdentification) {
        this.requesterIdentification = requesterIdentification;
    }

    public String getRequesterEntityName() {
        return this.requesterEntityName;
    }

    public void setRequesterEntityName(String requesterEntityName) {
        this.requesterEntityName = requesterEntityName;
    }

    public String getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(String requesterEntityType) {
        this.requesterEntityType = requesterEntityType;
    }

    public String getRequesterName() {
        return this.requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterPhoneNumber() {
        return this.requesterPhoneNumber;
    }

    public void setRequesterPhoneNumber(String requesterPhoneNumber) {
        this.requesterPhoneNumber = requesterPhoneNumber;
    }

    public String getRequesterEmail() {
        return this.requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
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

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
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

    public Double getOrderValue() {
        return this.orderValue;
    }

    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }

    public Integer getOrderRating() {
        return this.orderRating;
    }

    public void setOrderRating(Integer orderRating) {
        this.orderRating = orderRating;
    }

    public String getOwnerIdentification() {
        return this.ownerIdentification;
    }

    public void setOwnerIdentification(String ownerIdentification) {
        this.ownerIdentification = ownerIdentification;
    }

    public String getPaymentDetailsId() {
        return this.paymentDetailsId;
    }

    public void setPaymentDetailsId(String paymentDetailsId) {
        this.paymentDetailsId = paymentDetailsId;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Boolean isIsOrderTrackingActive() {
        return this.isOrderTrackingActive;
    }

    public Boolean getIsOrderTrackingActive() {
        return this.isOrderTrackingActive;
    }

    public void setIsOrderTrackingActive(Boolean isOrderTrackingActive) {
        this.isOrderTrackingActive = isOrderTrackingActive;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public OrderShortHistoryResponseDto getHistory() {
        return this.history;
    }

    public void setHistory(OrderShortHistoryResponseDto history) {
        this.history = history;
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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}