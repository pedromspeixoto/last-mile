package com.lastmile.customerservice.service.exception;

public class InvalidApiKeyException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "invalid api key for customer: ";

    public InvalidApiKeyException(String customerId) {
        super(DEFAULT_MESSAGE_PREFIX + customerId);
    }

    public InvalidApiKeyException(String customerId, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + customerId, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}