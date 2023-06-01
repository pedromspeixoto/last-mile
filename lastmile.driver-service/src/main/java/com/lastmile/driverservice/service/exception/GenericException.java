package com.lastmile.driverservice.service.exception;

public class GenericException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "Message: ";

    public GenericException(String method, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + method, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}