package com.lastmile.accountservice.service.exception;

public class UserNotActiveException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "user is not active: ";

    public UserNotActiveException(String id) {
	super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public UserNotActiveException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
