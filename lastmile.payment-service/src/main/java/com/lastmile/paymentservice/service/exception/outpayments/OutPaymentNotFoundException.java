package com.lastmile.paymentservice.service.exception.outpayments;

public class OutPaymentNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find out payment with id: ";

    public OutPaymentNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public OutPaymentNotFoundException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}