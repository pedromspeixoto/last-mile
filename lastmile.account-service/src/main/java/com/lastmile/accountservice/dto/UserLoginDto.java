package com.lastmile.accountservice.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class UserLoginDto {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    private CreateAccountDeviceRequestDto accountDeviceRequestDto;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CreateAccountDeviceRequestDto getAccountDeviceRequestDto() {
        return this.accountDeviceRequestDto;
    }

    public void setAccountDeviceRequestDto(CreateAccountDeviceRequestDto accountDeviceRequestDto) {
        this.accountDeviceRequestDto = accountDeviceRequestDto;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
