package com.lastmile.orderservice.service.exception;

public class InvalidScheduledDateException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "invalid scheduled date: ";

    public InvalidScheduledDateException(String date) {
        super(DEFAULT_MESSAGE_PREFIX + date);
    }

    public InvalidScheduledDateException(String date, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + date, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
