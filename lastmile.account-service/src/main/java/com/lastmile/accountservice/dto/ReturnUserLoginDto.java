package com.lastmile.accountservice.dto;

import java.io.Serializable;

import net.logstash.logback.encoder.org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ReturnUserLoginDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userIdentification;

    private String username;

    private String role;

    private String token;

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
