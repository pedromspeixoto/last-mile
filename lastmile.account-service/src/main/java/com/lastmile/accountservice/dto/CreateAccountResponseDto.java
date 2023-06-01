package com.lastmile.accountservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateAccountResponseDto {

    private String userIdentification;

    public CreateAccountResponseDto() {
    }

    public CreateAccountResponseDto(String userIdentification) {
        this.userIdentification = userIdentification;
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