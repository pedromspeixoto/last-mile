package com.lastmile.driverservice.service.exception;

public class LinkNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "link not found for fiscal entity id: ";

    public LinkNotFoundException(String fiscalEntityId, String userId) {
        super(DEFAULT_MESSAGE_PREFIX + fiscalEntityId + " and user id: " + userId);
    }

    public LinkNotFoundException(String fiscalEntityId, String userId, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + fiscalEntityId + " and user id: " + userId, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}