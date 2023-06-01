package com.lastmile.customerservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PatchCustomerValidatedRequestDto {

    private Boolean entityValidated;

    public Boolean isEntityValidated() {
        return this.entityValidated;
    }

    public Boolean getEntityValidated() {
        return this.entityValidated;
    }

    public void setEntityValidated(Boolean entityValidated) {
        this.entityValidated = entityValidated;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}