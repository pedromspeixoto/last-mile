package com.lastmile.utils.exceptions;

public class DocumentNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE_PREFIX_MODEL = "document not found exception: ";

    public DocumentNotFoundException(String service, Throwable cause) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + service, cause);
    }

    public DocumentNotFoundException(String service) {
        super(DEFAULT_MESSAGE_PREFIX_MODEL + service);
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }

}