package com.lastmile.orderservice.service.exception;

public class StatusTransitionNotAllowedException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX = "can't transition from status ";
    private static final String DEFAULT_MESSAGE_POSTFIX = " to new status ";

    public StatusTransitionNotAllowedException(String oldStatus, String newStatus) {
	super(DEFAULT_MESSAGE_PREFIX + oldStatus + DEFAULT_MESSAGE_POSTFIX + newStatus);
    }

    public StatusTransitionNotAllowedException(String oldStatus, String newStatus, Throwable cause) {
	super(DEFAULT_MESSAGE_PREFIX + oldStatus + DEFAULT_MESSAGE_POSTFIX + newStatus, cause);
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

}