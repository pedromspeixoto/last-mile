package com.lastmile.driverservice.service.exception;

public class DriverForbiddenException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "forbidden. user id: ";

    public DriverForbiddenException(String userId) {
        super(DEFAULT_MESSAGE_PREFIX + userId);
    }

    public DriverForbiddenException(String userId, String driverId) {
        super(DEFAULT_MESSAGE_PREFIX + userId + " and driver id: " + driverId);
    }

    public DriverForbiddenException(String userId, String driverId, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + userId + " and driver id: " + driverId, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}