package com.lastmile.accountservice.service.exception;

public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find user with id: ";

    public UserNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public UserNotFoundException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
