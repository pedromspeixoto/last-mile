package com.lastmile.accountservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.validations.ValidBirthDate;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidPhoneNumber;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class UpdateAccountRequestDto {

    @ValidPhoneNumber
    private String phoneNumber;

    @ValidEmail
    private String email;

    private String firstName;

    private String lastName;

    @JsonFormat(pattern="yyyy-MM-dd")
    @ValidBirthDate
    private Date birthDate;

    private String activeAddressId;

    private String activeBillingAddressId;

    private String activePaymentDetailsId;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}