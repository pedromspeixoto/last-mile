package com.lastmile.orderservice.enums;

public enum RequesterEntityType {

    MARKETPLACE, ACCOUNT, FISCALENTITY;

    public String getRequesterEntityType() {
        return name();
    }
}