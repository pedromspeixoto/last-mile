package com.lastmile.addressservice.enums;

public enum AddressType {

    HOME, BUSINESS, PERSONAL, MARKETPLACE;

    public String getAddressType() {
        return name();
    }
}