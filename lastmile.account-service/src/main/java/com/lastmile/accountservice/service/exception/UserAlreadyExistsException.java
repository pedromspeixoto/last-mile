package com.lastmile.accountservice.service.exception;

public class UserAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "user with this username already exists: ";

    public UserAlreadyExistsException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public UserAlreadyExistsException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
