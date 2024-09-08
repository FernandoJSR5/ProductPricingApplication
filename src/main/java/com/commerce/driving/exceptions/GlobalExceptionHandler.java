package com.commerce.driving.exceptions;

import com.commerce.application.exceptions.PriceException;
import com.commerce.domain.model.ErrorResponse;
import com.commerce.domain.model.PriceConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
     * Handle method argument type mismatch exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Parameter '%s' should be of type '%s'", ex.getName(), ex.getRequiredType().getSimpleName());
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, PriceConstants.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle missing servlet request parameter exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String errorMessage = String.format("Missing required parameter: '%s'", ex.getParameterName());
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, PriceConstants.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exception response entity.
     *
     * @return the response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException() {
        ErrorResponse errorResponse = new ErrorResponse(PriceConstants.ERROR_INTERNAL_SERVER_ERROR,
                PriceConstants.ERROR_CODE_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

