package com.lastmile.driverservice.service.exception;

public class FiscalEntityAlreadyExistsException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "fiscal entity already exits for user_identification ";

    public FiscalEntityAlreadyExistsException(String userId) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + userId);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}