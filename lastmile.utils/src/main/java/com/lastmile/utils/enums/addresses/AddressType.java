package com.lastmile.utils.enums.addresses;

public enum AddressType {

    HOME, BUSINESS, PERSONAL, MARKETPLACE;

    public String getAddressType() {
        return name();
    }
}