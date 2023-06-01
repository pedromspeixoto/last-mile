package com.lastmile.orderengine.service.exception;

public class DriversNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "driver not found for order identification: ";

    public DriversNotFoundException(String orderIdentification) {
	    super(DEFAULT_MESSAGE_PREFIX + orderIdentification);
    }

    public DriversNotFoundException(String orderIdentification, Throwable cause) {
	    super(DEFAULT_MESSAGE_PREFIX + orderIdentification, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}