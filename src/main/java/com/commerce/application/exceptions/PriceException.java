package com.commerce.application.exceptions;

/**
 * The type Price exception.
 */
public class PriceException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a new PriceException with the specified detail message and error code.
     *
     * @param message   the detail message
     * @param errorCode the error code
     */
    public PriceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new PriceException with the specified detail message, cause, and error code.
     *
     * @param message   the detail message
     * @param cause     the cause of the exception
     * @param errorCode the error code
     */
    public PriceException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
