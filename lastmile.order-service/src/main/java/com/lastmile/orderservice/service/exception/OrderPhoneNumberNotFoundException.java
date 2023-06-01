package com.lastmile.orderservice.service.exception;

public class OrderPhoneNumberNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find phone number in any order: ";

    public OrderPhoneNumberNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public OrderPhoneNumberNotFoundException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
