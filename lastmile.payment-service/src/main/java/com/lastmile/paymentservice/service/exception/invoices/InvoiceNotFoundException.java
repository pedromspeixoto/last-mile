package com.lastmile.paymentservice.service.exception.invoices;

public class InvoiceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "could not find invoice with id: ";

    public InvoiceNotFoundException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public InvoiceNotFoundException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}