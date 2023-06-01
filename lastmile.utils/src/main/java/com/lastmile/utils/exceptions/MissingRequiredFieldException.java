package com.lastmile.utils.exceptions;

public class MissingRequiredFieldException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "missing required field: ";

    public MissingRequiredFieldException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public MissingRequiredFieldException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}