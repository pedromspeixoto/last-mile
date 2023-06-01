package com.lastmile.paymentservice.service.exception.outpayments;

public class OutPaymentForbiddenException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "forbidden.";

    public OutPaymentForbiddenException() {
        super(DEFAULT_MESSAGE_PREFIX);
    }

    public OutPaymentForbiddenException(Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}