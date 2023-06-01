package com.lastmile.accountservice.service.exception;

public class UserAlreadyActiveException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "user is already active: ";

    public UserAlreadyActiveException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public UserAlreadyActiveException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
