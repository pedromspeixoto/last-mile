package com.lastmile.accountservice.service.exception;

public class InvalidActivationCodeException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "invalid activation code: ";

    public InvalidActivationCodeException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public InvalidActivationCodeException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}