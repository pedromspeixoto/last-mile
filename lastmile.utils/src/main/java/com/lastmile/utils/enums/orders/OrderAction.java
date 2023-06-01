package com.lastmile.utils.enums.orders;

public enum OrderAction {

    ACCEPT, REJECT, PICKUP, FINALIZE;

    public String getOrderAction() {
        return name();
    }
}