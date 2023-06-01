package com.lastmile.accountservice.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class UserUpdatePasswordRequestDto {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "New password is mandatory")
    private String newPassword;

    @NotBlank(message = "Activation code is mandatory")
    private String activationCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
