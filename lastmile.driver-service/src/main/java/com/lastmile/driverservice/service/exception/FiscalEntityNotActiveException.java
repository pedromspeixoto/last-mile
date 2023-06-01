package com.lastmile.driverservice.service.exception;

public class FiscalEntityNotActiveException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "fiscal entity is not active: ";

    public FiscalEntityNotActiveException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public FiscalEntityNotActiveException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
