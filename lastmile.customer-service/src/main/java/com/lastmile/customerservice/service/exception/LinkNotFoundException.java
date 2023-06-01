package com.lastmile.customerservice.service.exception;

public class LinkNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "link not found for customer id: ";

    public LinkNotFoundException(String customerId, String userId) {
        super(DEFAULT_MESSAGE_PREFIX + customerId + " and user id: " + userId);
    }

    public LinkNotFoundException(String customerId, String userId, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + customerId + " and user id: " + userId, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
