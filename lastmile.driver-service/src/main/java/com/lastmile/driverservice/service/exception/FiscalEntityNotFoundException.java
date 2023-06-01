package com.lastmile.driverservice.service.exception;

public class FiscalEntityNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find fiscal entity with id: ";

    public FiscalEntityNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public FiscalEntityNotFoundException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
