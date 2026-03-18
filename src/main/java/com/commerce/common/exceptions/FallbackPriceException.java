package com.commerce.common.exceptions;

import com.commerce.common.enums.CommonErrorCodes;

public class FallbackPriceException extends BasePriceException {
    public FallbackPriceException() {
        super(CommonErrorCodes.FALLBACK.getMessage(), CommonErrorCodes.FALLBACK.getCode());
    }
}
