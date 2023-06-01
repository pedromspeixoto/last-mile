package com.lastmile.customerservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.enums.CustomerStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class CustomerDto {

    private String customerIdentification;

    private String name;

    private String publicName;

    private String nif;

    private String customerEmail;

    private String customerPhoneNumber;

    private String customerWebsite;

    private String activeAddressId;

    private String activeBillingAddressId;

    private String activePaymentDetailsId;

    private CustomerStatus status;

    private String customerCallbackUrl;

    private Boolean entityValidated;

    public String getCustomerIdentification() {
        return this.customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
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

    public CustomerStatus getStatus() {
        return this.status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public Boolean isEntityValidated() {
        return this.entityValidated;
    }

    public Boolean getEntityValidated() {
        return this.entityValidated;
    }

    public void setEntityValidated(Boolean entityValidated) {
        this.entityValidated = entityValidated;
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