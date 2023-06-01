package com.lastmile.customerservice.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidPhoneNumber;
import com.lastmile.utils.validations.ValidUrl;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class CreateCustomerRequestDto {

    private String userIdentification;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Public Name is mandatory")
    private String publicName;

    @NotBlank(message = "NIF is mandatory")
    private String nif;

    @ValidEmail
    private String customerEmail;

    @ValidPhoneNumber
    private String customerPhoneNumber;

    private String customerWebsite;

    private CreateAddressRequestDto activeAddress;

    private CreateAddressRequestDto activeBillingAddress;

    @ValidUrl
    private String customerCallbackUrl;

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicName() {
        return this.publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getNif() {
        return this.nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return this.customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerWebsite() {
        return this.customerWebsite;
    }

    public void setCustomerWebsite(String customerWebsite) {
        this.customerWebsite = customerWebsite;
    }

    public CreateAddressRequestDto getActiveAddress() {
        return this.activeAddress;
    }

    public void setActiveAddress(CreateAddressRequestDto activeAddress) {
        this.activeAddress = activeAddress;
    }

    public CreateAddressRequestDto getActiveBillingAddress() {
        return this.activeBillingAddress;
    }

    public void setActiveBillingAddress(CreateAddressRequestDto activeBillingAddress) {
        this.activeBillingAddress = activeBillingAddress;
    }

    public String getCustomerCallbackUrl() {
        return this.customerCallbackUrl;
    }

    public void setCustomerCallbackUrl(String customerCallbackUrl) {
        this.customerCallbackUrl = customerCallbackUrl;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}