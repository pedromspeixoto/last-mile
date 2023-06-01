package com.lastmile.orderservice.service.exception;

public class NoAssignedDriverException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "no assigned driver for order: ";

    public NoAssignedDriverException(String id, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + id, cause);
    }

    public NoAssignedDriverException(String id) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + id);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}