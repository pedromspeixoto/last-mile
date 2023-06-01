package com.lastmile.orderservice.service.exception;

public class GoogleAPIException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "error calling Google API";

    public GoogleAPIException(Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL, cause);
    }

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}