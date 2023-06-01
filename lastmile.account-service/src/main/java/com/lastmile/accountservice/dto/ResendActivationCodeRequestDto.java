package com.lastmile.accountservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ResendActivationCodeRequestDto {

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}