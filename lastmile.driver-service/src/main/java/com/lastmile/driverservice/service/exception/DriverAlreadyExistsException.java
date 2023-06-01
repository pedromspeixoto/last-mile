package com.lastmile.driverservice.service.exception;

public class DriverAlreadyExistsException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "driver already exits for user_identification ";

    public DriverAlreadyExistsException(String userId) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + userId);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}