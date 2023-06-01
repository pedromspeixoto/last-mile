package com.lastmile.paymentservice.service.exception.payments;

public class PaymentNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find payment with id: ";

    public PaymentNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public PaymentNotFoundException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}