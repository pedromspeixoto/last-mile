package com.lastmile.customerservice.service.exception;

public class WarehouseNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find warehouse with id: ";

    public WarehouseNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public WarehouseNotFoundException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}