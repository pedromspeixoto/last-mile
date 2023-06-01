package com.lastmile.gatewayservice.client.exception;

public class UnirestException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnirestException(String message, Throwable cause) {
        super(message, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}