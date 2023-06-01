package com.lastmile.accountservice.dto;

import java.io.Serializable;

import com.lastmile.accountservice.enums.Authorities;

public class AuthUserRegistrationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userIdentification;

    private String username;

    private String password;

    private Authorities role;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Authorities getRole() {
        return this.role;
    }

    public void setRole(Authorities role) {
        this.role = role;
    }

}
