package com.lastmile.customerservice.service.exception;

public class CustomerNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find customer with id: ";

    public CustomerNotFoundException(String id) {
	super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public CustomerNotFoundException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
