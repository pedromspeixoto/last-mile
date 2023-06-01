package com.lastmile.addressservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateAddressResponseDto {

    private String addressIdentification;

    public CreateAddressResponseDto() {
    }

    public CreateAddressResponseDto(String addressIdentification) {
        this.addressIdentification = addressIdentification;
    }

    public String getAddressIdentification() {
        return this.addressIdentification;
    }

    public void setAddressIdentification(String addressIdentification) {
        this.addressIdentification = addressIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}