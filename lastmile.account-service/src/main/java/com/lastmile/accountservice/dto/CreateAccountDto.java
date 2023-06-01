package com.lastmile.accountservice.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lastmile.accountservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.accountservice.enums.AccountType;
import com.lastmile.utils.validations.ValidBirthDate;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidPhoneNumber;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateAccountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Account type is mandatory")
    private AccountType accountType;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @ValidEmail
    private String email;

    @ValidPhoneNumber
    private String phoneNumber;

    private String firstName;

    private String lastName;

    @JsonFormat(pattern="yyyy-MM-dd")
    @ValidBirthDate
    private Date birthDate;

    private CreateAddressRequestDto address;

    private CreateAddressRequestDto billingAddress;

    public String getPassword() {
        return password;
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

    public void setPassword(String password) {
        this.password = password;
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

    public AccountType getAccountType() {
        return this.accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public CreateAddressRequestDto getAddress() {
        return this.address;
    }

    public void setAddress(CreateAddressRequestDto address) {
        this.address = address;
    }

    public CreateAddressRequestDto getBillingAddress() {
        return this.billingAddress;
    }

    public void setBillingAddress(CreateAddressRequestDto billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}