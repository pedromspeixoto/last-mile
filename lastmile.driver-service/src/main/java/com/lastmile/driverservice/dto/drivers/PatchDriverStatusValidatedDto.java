package com.lastmile.driverservice.dto.drivers;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PatchDriverStatusValidatedDto {

    private Boolean validated;

    public Boolean isValidated() {
        return this.validated;
    }

    public Boolean getValidated() {
        return this.validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}