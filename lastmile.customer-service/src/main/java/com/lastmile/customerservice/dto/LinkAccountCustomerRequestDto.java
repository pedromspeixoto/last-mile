package com.lastmile.customerservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class LinkAccountCustomerRequestDto {

    private String userIdentification;


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