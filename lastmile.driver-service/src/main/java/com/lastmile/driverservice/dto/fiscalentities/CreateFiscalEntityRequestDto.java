package com.lastmile.driverservice.dto.fiscalentities;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.driverservice.enums.PaymentFrequency;
import com.lastmile.utils.validations.ValidCountryCode;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidIban;
import com.lastmile.utils.validations.ValidPhoneNumber;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class CreateFiscalEntityRequestDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @ValidEmail
    private String email;

    @NotBlank(message = "Fiscal entity phone number is mandatory")
    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Fiscal entity fiscal number is mandatory")
    private String fiscalNumber;

    @NotBlank(message = "Bank account holder name is mandatory")
    private String bankAccountHolderName;

    @NotBlank(message = "Bank account iban is mandatory")
    @ValidIban
    private String bankAccountIban;

    @NotBlank(message = "Bank account country code is mandatory")
    @ValidCountryCode
    private String bankAccountCountryCode;

    @NotNull(message = "Payment frequency is mandatory")
    private PaymentFrequency paymentFrequency;

    private CreateAddressRequestDto activeAddress;

    private CreateAddressRequestDto activeBillingAddress;

    private String userIdentification;

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

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}