package com.commerce.driving.exceptions;

import com.commerce.application.exceptions.PriceException;
import com.commerce.domain.model.ErrorResponse;
import com.commerce.domain.model.PriceConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The type Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle price exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(PriceException.class)
    public ResponseEntity<ErrorResponse> handlePriceException(PriceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle generic exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(PriceConstants.ERROR_INTERNAL_SERVER_ERROR,
                PriceConstants.ERROR_CODE_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

