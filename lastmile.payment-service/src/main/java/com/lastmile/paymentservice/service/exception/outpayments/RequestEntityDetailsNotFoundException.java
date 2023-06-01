package com.lastmile.paymentservice.service.exception.outpayments;

public class RequestEntityDetailsNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "no details found for requester identification: ";

    public RequestEntityDetailsNotFoundException(String requesterIdentification) {
        super(DEFAULT_MESSAGE_PREFIX + requesterIdentification);
    }

    public RequestEntityDetailsNotFoundException(String requesterIdentification, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + requesterIdentification, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}