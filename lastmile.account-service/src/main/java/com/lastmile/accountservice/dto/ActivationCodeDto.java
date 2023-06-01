package com.lastmile.accountservice.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ActivationCodeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String activationCode;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActivationCode() {
        return this.activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}