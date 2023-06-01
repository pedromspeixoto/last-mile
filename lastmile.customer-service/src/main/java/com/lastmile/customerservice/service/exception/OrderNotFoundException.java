package com.lastmile.customerservice.service.exception;

public class OrderNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find order with id: ";

    public OrderNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public OrderNotFoundException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
