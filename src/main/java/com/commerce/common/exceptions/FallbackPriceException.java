package com.commerce.common.exceptions;

import com.commerce.common.enums.CommonErrorCodes;

/**
 * The type Fallback price exception.
 */
public class FallbackPriceException extends BasePriceException {

    /**
     * Instantiates a new Fallback price exception.
     */
    public FallbackPriceException() {
        super(CommonErrorCodes.FALLBACK.getMessage(), CommonErrorCodes.FALLBACK.getCode());
    }
}
