package com.lastmile.orderservice.service.exception;

public class PricingException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "error calculating pricing. ";

    public PricingException(String message, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + message, cause);
    }

    public PricingException(Throwable cause) {
        super(cause);
    }

    public PricingException() {
        super(DEFAULT_MESSAGE_PREFIX_MODEL);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}