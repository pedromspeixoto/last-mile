package com.lastmile.utils.enums;

public enum EntityType {

    ACCOUNT, DRIVER, MARKETPLACE, FISCALENTITY;

    public String getEntityType() {
        return name();
    }
}