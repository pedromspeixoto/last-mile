package com.lastmile.orderservice.service.exception;

public class PaymentProcessException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "error processing payment for order: ";

    public PaymentProcessException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public PaymentProcessException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
