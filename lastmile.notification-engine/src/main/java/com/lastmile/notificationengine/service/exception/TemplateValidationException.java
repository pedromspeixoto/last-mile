package com.lastmile.notificationengine.service.exception;

public class TemplateValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "invalid template: ";

    public TemplateValidationException(String message) {
	super(DEFAULT_MESSAGE_PREFIX + message);
    }

    public TemplateValidationException(String message, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + message, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
