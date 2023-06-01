package com.lastmile.orderservice.service.exception;

public class FeignCommunicationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "feign communication error calling ";

    public FeignCommunicationException(String service, String message, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + service + " error message: " + message, cause);
    }

    public FeignCommunicationException(String service, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + service, cause);
    }

    public FeignCommunicationException(String service) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + service);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}