package com.commerce.common.exceptions;

public class BasePriceException extends RuntimeException {

    private final String errorCode;

    public BasePriceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
