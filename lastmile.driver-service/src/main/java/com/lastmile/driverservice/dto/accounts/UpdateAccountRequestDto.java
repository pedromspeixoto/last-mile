package com.lastmile.driverservice.dto.accounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.enums.Authorities;
import com.lastmile.utils.validations.ValidEmail;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class UpdateAccountRequestDto {

    @ValidEmail
    private String email;

    private String firstName;

    private String lastName;

    private String identificationNumber;

    private Authorities role;

    public Authorities getRole() {
        return this.role;
    }

    public void setRole(Authorities role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
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

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}