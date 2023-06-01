package com.lastmile.addressservice.service.exception;

public class AddressNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find address with id: ";

    public AddressNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public AddressNotFoundException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}