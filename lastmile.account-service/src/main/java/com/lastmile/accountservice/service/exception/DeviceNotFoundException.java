package com.lastmile.accountservice.service.exception;

public class DeviceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find device for user: ";

    public DeviceNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public DeviceNotFoundException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
