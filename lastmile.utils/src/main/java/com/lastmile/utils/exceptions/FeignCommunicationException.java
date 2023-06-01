package com.lastmile.utils.exceptions;

public class FeignCommunicationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "feign communication error. error message: ";

    public FeignCommunicationException(String message, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + message, cause);
    }

    public FeignCommunicationException(String message) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + message);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}