package com.lastmile.paymentservice.service.exception.paymentdetails;

public class EasyPayException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "error calling easy pay external service. message: ";

    public EasyPayException(String message) {
        super(DEFAULT_MESSAGE_PREFIX + message);
    }

    public EasyPayException(String message, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + message, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}