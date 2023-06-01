package com.lastmile.utils.enums;

public enum Authorities {

    ROLE_USER, ROLE_DRIVER, ROLE_CUSTOMER, ROLE_ADMIN;

    public String getAuthority() {
        return name();
    }
}