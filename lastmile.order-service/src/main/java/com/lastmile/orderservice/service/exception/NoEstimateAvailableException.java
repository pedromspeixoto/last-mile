package com.lastmile.orderservice.service.exception;

public class NoEstimateAvailableException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not calculate estimate. message: ";

    public NoEstimateAvailableException(String message) {
	    super(DEFAULT_MESSAGE_PREFIX + message);
    }

    public NoEstimateAvailableException(String message, Throwable cause) {
	    super(DEFAULT_MESSAGE_PREFIX + message, cause);
    }

    public static long getSerialversionuid() {
	    return serialVersionUID;
    }

}