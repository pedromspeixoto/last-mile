package com.lastmile.customerservice.service.exception;

public class EntityNotValidatedException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "entity not validated.";

    public EntityNotValidatedException() {
        super(DEFAULT_MESSAGE_PREFIX);
    }

    public EntityNotValidatedException(Throwable cause) {
        super(cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}