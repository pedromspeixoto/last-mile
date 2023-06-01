package com.lastmile.accountservice.enums;

public enum AccountType {

    MOBILE, WEB;

    public String getAccountType() {
        return name();
    }
}
