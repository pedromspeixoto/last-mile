package com.lastmile.orderservice.dto;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class LinkUserToOrderRequestDto {

    @NotBlank(message = "User identification is mandatory")
    private String userIdentification;

    @NotBlank(message = "Short order id is mandatory")
    private String shortOrderId;

    @NotBlank(message = "Phone number is mandatory")
    private String phoneNumber;

    public LinkUserToOrderRequestDto() {
    }

    public LinkUserToOrderRequestDto(String userIdentification, String shortOrderId, String phoneNumber) {
        this.userIdentification = userIdentification;
        this.shortOrderId = shortOrderId;
        this.phoneNumber = phoneNumber;
    }

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getShortOrderId() {
        return this.shortOrderId;
    }

    public void setShortOrderId(String shortOrderId) {
        this.shortOrderId = shortOrderId;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}