package com.lastmile.utils.exceptions;

public class ExternalServerException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "error calling external service: ";

    public ExternalServerException(String service, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + service, cause);
    }

    public ExternalServerException(String service) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + service);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}