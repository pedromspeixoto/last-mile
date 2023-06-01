package com.lastmile.utils.exceptions;

public class RabbitException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "error publishing in rabbitmq. error: ";

    public RabbitException(String message) {
        super(DEFAULT_MESSAGE_PREFIX + message);
    }

    public RabbitException(String message, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + message, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}