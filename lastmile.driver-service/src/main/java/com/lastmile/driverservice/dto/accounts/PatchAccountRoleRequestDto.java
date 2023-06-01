package com.lastmile.driverservice.dto.accounts;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PatchAccountRoleRequestDto {

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}