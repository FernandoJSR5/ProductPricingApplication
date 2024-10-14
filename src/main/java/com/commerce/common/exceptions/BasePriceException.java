package com.commerce.common.exceptions;

/**
 * The type Base price exception.
 */
public class BasePriceException extends RuntimeException {

    private final String errorCode;

    /**
     * Instantiates a new Base price exception.
     *
     * @param message   the message
     * @param errorCode the error code
     */
    public BasePriceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Gets error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
