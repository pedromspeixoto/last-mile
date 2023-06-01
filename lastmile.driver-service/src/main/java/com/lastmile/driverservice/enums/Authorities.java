package com.lastmile.driverservice.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Authorities implements GrantedAuthority {

    ROLE_USER, ROLE_DRIVER, ROLE_FISCAL_ENTITY, ROLE_CUSTOMER, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
