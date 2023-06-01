package com.lastmile.accountservice.service.exception;

public class InvalidLoginException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "invalid credentials for username: ";

    public InvalidLoginException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public InvalidLoginException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}