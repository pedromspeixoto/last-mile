package com.lastmile.customerservice.service.exception;

public class CustomerInvalidStatusException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "transition not allowed. original status: ";

    public CustomerInvalidStatusException(String oldStatus, String newStatus) {
        super(DEFAULT_MESSAGE_PREFIX + oldStatus + " new status: " + newStatus);
    }

    public CustomerInvalidStatusException(String oldStatus, String newStatus, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + oldStatus + " new status: " + newStatus, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}