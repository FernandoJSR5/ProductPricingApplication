package com.commerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Common error codes.
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCodes {
    /**
     * The Not found.
     */
    NOT_FOUND("404", "No applicable price found for the provided parameters."),
    /**
     * The Fallback.
     */
    FALLBACK("503", "The service is currently unavailable, please try again later."),
    /**
     * The Internal server error.
     */
    INTERNAL_SERVER_ERROR("Internal Server Error", "500"),
    /**
     * The Bad request.
     */
    BAD_REQUEST("Bad Request", "400");


    private final String code;
    private final String message;

}