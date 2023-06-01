package com.lastmile.accountservice.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class GetAccountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userIdentification;

    private String username;

    private String email;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthDate;

    private byte[] profilePicture;

    private String activeAddressId;

    private String activeBillingAddressId;

    private String activePaymentDetailsId;

    private String role;

    private Boolean active;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public byte[] getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Boolean isActive() {
        return this.active;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}