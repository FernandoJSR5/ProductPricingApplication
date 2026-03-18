package com.commerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCodes {
    NOT_FOUND("404", "No applicable price found for the provided parameters."),
    FALLBACK("503", "The service is currently unavailable, please try again later."),
    INTERNAL_SERVER_ERROR("Internal Server Error", "500"),
    BAD_REQUEST("Bad Request", "400");

    private final String code;
    private final String message;

}