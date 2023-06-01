package com.lastmile.orderservice.service.exception;

public class OrderForbiddenException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "forbidden. user id: ";

    public OrderForbiddenException() {
        super();
    }

    public OrderForbiddenException(Throwable cause) {
        super(cause);
    }

    public OrderForbiddenException(String id) {
        super(DEFAULT_MESSAGE_PREFIX + id);
    }

    public OrderForbiddenException(String id, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + id, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}
