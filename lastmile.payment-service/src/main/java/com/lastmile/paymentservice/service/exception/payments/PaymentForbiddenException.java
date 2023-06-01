package com.lastmile.paymentservice.service.exception.payments;

public class PaymentForbiddenException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "forbidden.";

    public PaymentForbiddenException() {
        super(DEFAULT_MESSAGE_PREFIX);
    }

    public PaymentForbiddenException(Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}