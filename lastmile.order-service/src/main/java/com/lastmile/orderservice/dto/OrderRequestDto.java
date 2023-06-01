package com.lastmile.orderservice.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderservice.enums.OrderPriority;
import com.lastmile.orderservice.enums.OrderType;
import com.lastmile.orderservice.enums.RequesterEntityType;
import com.lastmile.utils.validations.ValidCountryCode;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidPhoneNumber;
import com.lastmile.utils.validations.ValidScheduledDate;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderRequestDto {

    private String orderExternalIdentification;

    @NotBlank(message = "Requester identification is mandatory")
    private String requesterIdentification;

    @NotBlank(message = "Requester entity name is mandatory")
    private String requesterEntityName;

    @NotNull(message = "Requester entity type is mandatory")
    private RequesterEntityType requesterEntityType;

    private String requesterName;

    @NotBlank(message = "Requester phone number is mandatory")
    @ValidPhoneNumber
    private String requesterPhoneNumber;

    @ValidEmail
    private String requesterEmail;

    @ValidCountryCode
    private String requesterCountry;

    private String requesterCity;

    private String requesterZipCode;

    @NotNull(message = "Pickup latitude is mandatory")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is mandatory")
    private Double pickupLongitude;

    @ValidCountryCode
    private String destinationCountry;

    private String destinationCity;

    private String destinationZipCode;

    @NotNull(message = "Destination latitude is mandatory")
    private Double destinationLatitude;

    @NotNull(message = "Destination longitude is mandatory")
    private Double destinationLongitude;

    @NotNull(message = "Order priority is mandatory")
    private OrderPriority priority;

    @ValidScheduledDate
    private Date scheduledDate;

    @NotNull(message = "Order value is mandatory")
    private Double orderValue;

    @NotNull(message = "Order type is mandatory")
    private OrderType orderType;

    @NotBlank(message = "Payment details are mandatory")
    private String paymentDetailsId;

    private Integer packagingEta;

    public String getOrderExternalIdentification() {
        return this.orderExternalIdentification;
    }

    public void setOrderExternalIdentification(String orderExternalIdentification) {
        this.orderExternalIdentification = orderExternalIdentification;
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

    public RequesterEntityType getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(RequesterEntityType requesterEntityType) {
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

    public Double getOrderValue() {
        return this.orderValue;
    }

    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getPaymentDetailsId() {
        return this.paymentDetailsId;
    }

    public void setPaymentDetailsId(String paymentDetailsId) {
        this.paymentDetailsId = paymentDetailsId;
    }

    public Integer getPackagingEta() {
        return this.packagingEta;
    }

    public void setPackagingEta(Integer packagingEta) {
        this.packagingEta = packagingEta;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}