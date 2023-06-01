package com.lastmile.accountservice.dto.addresses;

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

}