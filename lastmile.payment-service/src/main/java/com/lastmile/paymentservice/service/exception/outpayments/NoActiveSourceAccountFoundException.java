package com.lastmile.paymentservice.service.exception.outpayments;

public class NoActiveSourceAccountFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "no active account found for this country code: ";

    public NoActiveSourceAccountFoundException(String countryCode) {
        super(DEFAULT_MESSAGE_PREFIX + countryCode);
    }

    public NoActiveSourceAccountFoundException(String countryCode, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + countryCode, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}