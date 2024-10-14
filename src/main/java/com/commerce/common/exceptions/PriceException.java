package com.commerce.common.exceptions;

import com.commerce.common.enums.CommonErrorCodes;

/**
 * The type Price exception.
 */
public class PriceException extends BasePriceException {

    /**
     * Instantiates a new Price exception.
     */
    public PriceException() {
        super(CommonErrorCodes.NOT_FOUND.getMessage(), CommonErrorCodes.NOT_FOUND.getCode());
    }
}