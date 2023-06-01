package com.lastmile.orderservice.service.exception;

public class MissingFieldException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "missing field ";

    public MissingFieldException(String field, String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + field + " for id: " + id, cause);
    }

    public MissingFieldException(String field, String id) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + field + " for id: " + id);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}