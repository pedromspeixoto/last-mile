package com.lastmile.customerservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.enums.CustomerStatus;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidPhoneNumber;
import com.lastmile.utils.validations.ValidUrl;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class UpdateCustomerRequestDto {

    private String name;

    private String publicName;

    private String nif;

    @ValidEmail
    private String customerEmail;

    @ValidPhoneNumber
    private String customerPhoneNumber;

    private String customerWebsite;

    private String activeAddressId;

    private String activeBillingAddressId;

    private String activePaymentDetailsId;

    @ValidUrl
    private String customerCallbackUrl;

    private CustomerStatus status;

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

    public String getActiveAddressId() {
        return this.activeAddressId;
    }

    public void setActiveAddressId(String activeAddressId) {
        this.activeAddressId = activeAddressId;
    }

    public String getActiveBillingAddressId() {
        return this.activeBillingAddressId;
    }

    public void setActiveBillingAddressId(String activeBillingAddressId) {
        this.activeBillingAddressId = activeBillingAddressId;
    }

    public String getActivePaymentDetailsId() {
        return this.activePaymentDetailsId;
    }

    public void setActivePaymentDetailsId(String activePaymentDetailsId) {
        this.activePaymentDetailsId = activePaymentDetailsId;
    }

    public String getCustomerCallbackUrl() {
        return this.customerCallbackUrl;
    }

    public void setCustomerCallbackUrl(String customerCallbackUrl) {
        this.customerCallbackUrl = customerCallbackUrl;
    }

    public CustomerStatus getStatus() {
        return this.status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}