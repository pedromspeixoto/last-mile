package com.lastmile.utils.enums.orders;

public enum RequesterEntityType {

    MARKETPLACE, ACCOUNT;

    public String getDriverStatus() {
        return name();
    }
}