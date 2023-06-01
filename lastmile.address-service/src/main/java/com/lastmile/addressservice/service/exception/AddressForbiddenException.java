package com.lastmile.addressservice.service.exception;

public class AddressForbiddenException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "forbidden.";

    public AddressForbiddenException() {
        super(DEFAULT_MESSAGE_PREFIX);
    }

    public AddressForbiddenException(Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}