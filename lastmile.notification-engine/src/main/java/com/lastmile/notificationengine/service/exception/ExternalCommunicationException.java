package com.lastmile.notificationengine.service.exception;

public class ExternalCommunicationException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "external communication error: ";

    public ExternalCommunicationException(String message) {
	super(DEFAULT_MESSAGE_PREFIX + message);
    }

    public ExternalCommunicationException(String message, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + message, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}