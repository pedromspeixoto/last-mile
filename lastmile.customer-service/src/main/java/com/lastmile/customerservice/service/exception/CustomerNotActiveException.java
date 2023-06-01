package com.lastmile.customerservice.service.exception;

public class CustomerNotActiveException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "customer is not active. id: ";

    public CustomerNotActiveException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public CustomerNotActiveException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
