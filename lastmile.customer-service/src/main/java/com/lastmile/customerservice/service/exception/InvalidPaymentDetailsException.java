package com.lastmile.customerservice.service.exception;

public class InvalidPaymentDetailsException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "invalid payment details for customer: ";

    public InvalidPaymentDetailsException(String customerId) {
        super(DEFAULT_MESSAGE_PREFIX + customerId);
    }

    public InvalidPaymentDetailsException(String customerId, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + customerId, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}