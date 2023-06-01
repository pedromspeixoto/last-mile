package com.lastmile.driverservice.service.exception;

public class VehicleNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find vehicle with id: ";

    public VehicleNotFoundException(String id) {
	super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public VehicleNotFoundException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
