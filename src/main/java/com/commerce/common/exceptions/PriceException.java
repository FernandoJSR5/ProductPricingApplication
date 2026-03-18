package com.commerce.common.exceptions;

import com.commerce.common.enums.CommonErrorCodes;

public class PriceException extends BasePriceException {

    public PriceException() {
        super(CommonErrorCodes.NOT_FOUND.getMessage(), CommonErrorCodes.NOT_FOUND.getCode());
    }
}