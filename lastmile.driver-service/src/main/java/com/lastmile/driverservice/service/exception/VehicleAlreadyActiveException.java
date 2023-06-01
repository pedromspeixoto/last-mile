package com.lastmile.driverservice.service.exception;

public class VehicleAlreadyActiveException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "driver already has an active vehicle: ";

    public VehicleAlreadyActiveException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public VehicleAlreadyActiveException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
