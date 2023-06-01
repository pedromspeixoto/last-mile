package com.lastmile.driverservice.service.exception;

public class CustomFeignException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "feign communication error. method: ";

    public CustomFeignException(String method, Integer httpStatus, String reason) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + method + "; http status: " + httpStatus + "; reason: " + reason);
    }

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}