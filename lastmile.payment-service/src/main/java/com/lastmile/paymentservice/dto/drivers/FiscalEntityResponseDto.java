package com.lastmile.paymentservice.dto.drivers;

import com.lastmile.utils.enums.drivers.FiscalEntityStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.lastmile.paymentservice.enums.PaymentFrequency;

public class FiscalEntityResponseDto {

    private String name;

    private String email;

    private String phoneNumber;

    private String fiscalNumber;

    private String bankAccountHolderName;

    private String bankAccountIban;

    private String bankAccountCountryCode;

    private PaymentFrequency paymentFrequency;

    private String activeAddressId;

    private String activeBillingAddressId;

    private FiscalEntityStatus status;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFiscalNumber() {
        return this.fiscalNumber;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getBankAccountHolderName() {
        return this.bankAccountHolderName;
    }

    public void setBankAccountHolderName(String bankAccountHolderName) {
        this.bankAccountHolderName = bankAccountHolderName;
    }

    public String getBankAccountIban() {
        return this.bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
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

    public FiscalEntityStatus getStatus() {
        return this.status;
    }

    public void setStatus(FiscalEntityStatus status) {
        this.status = status;
    }

    public String getBankAccountCountryCode() {
        return this.bankAccountCountryCode;
    }

    public void setBankAccountCountryCode(String bankAccountCountryCode) {
        this.bankAccountCountryCode = bankAccountCountryCode;
    }

    public PaymentFrequency getPaymentFrequency() {
        return this.paymentFrequency;
    }

    public void setPaymentFrequency(PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}