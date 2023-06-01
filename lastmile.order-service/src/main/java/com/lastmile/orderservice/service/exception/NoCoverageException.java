package com.lastmile.orderservice.service.exception;

public class NoCoverageException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "no coverage for the input coordinates received";

    public NoCoverageException() {
        super(DEFAULT_MESSAGE_PREFIX_MODEL);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}