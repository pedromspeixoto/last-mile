package com.lastmile.utils.exceptions;

public class UnauthorizedException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "cannot perform this action: ";

    public UnauthorizedException(String method, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + method, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}