package com.lastmile.driverservice.service.exception;

public class DriverStatusInvalidException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "invalid driver status: ";

    public DriverStatusInvalidException(String status) {
        super(DEFAULT_MESSAGE_PREFIX + status);
    }

    public DriverStatusInvalidException(String status, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + status, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}