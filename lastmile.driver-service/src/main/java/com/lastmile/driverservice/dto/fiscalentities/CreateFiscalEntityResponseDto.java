package com.lastmile.driverservice.dto.fiscalentities;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateFiscalEntityResponseDto {

    private String fiscalEntityIdentification;

    public CreateFiscalEntityResponseDto() {
    }

    public CreateFiscalEntityResponseDto(String fiscalEntityIdentification) {
        this.fiscalEntityIdentification = fiscalEntityIdentification;
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