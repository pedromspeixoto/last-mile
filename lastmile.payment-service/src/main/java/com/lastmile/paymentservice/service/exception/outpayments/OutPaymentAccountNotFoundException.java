package com.lastmile.paymentservice.service.exception.outpayments;

public class OutPaymentAccountNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find out payment account with id: ";

    public OutPaymentAccountNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public OutPaymentAccountNotFoundException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}