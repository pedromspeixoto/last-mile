package com.lastmile.notificationengine.service.exception;

public class PhoneVerificationException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "phone verification exception: ";

    public PhoneVerificationException(String message) {
	super(DEFAULT_MESSAGE_PREFIX + message);
    }

    public PhoneVerificationException(String message, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + message, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}