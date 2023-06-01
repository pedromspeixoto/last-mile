package com.lastmile.paymentservice.service.exception.outpayments;

public class OutPaymentAlreadyProcessedException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "outbound payment requested was already processed: ";

    public OutPaymentAlreadyProcessedException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public OutPaymentAlreadyProcessedException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}