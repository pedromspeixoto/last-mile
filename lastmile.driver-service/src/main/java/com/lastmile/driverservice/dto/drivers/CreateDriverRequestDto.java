package com.lastmile.driverservice.dto.drivers;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class CreateDriverRequestDto {

    @NotBlank(message = "User Identification is mandatory")
    private String userIdentification;

    private String fiscalEntityIdentification;

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getFiscalEntityIdentification() {
        return this.fiscalEntityIdentification;
    }

    public void setFiscalEntityIdentification(String fiscalEntityIdentification) {
        this.fiscalEntityIdentification = fiscalEntityIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}