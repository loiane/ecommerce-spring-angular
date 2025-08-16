package com.loiane.product.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when invalid input is provided to the API.
 * This is a business exception that maps to HTTP 400 Bad Request status.
 */
@Schema(description = "Exception thrown when invalid input is provided")
public class InvalidInputException extends BusinessException {

    private static final String ERROR_CODE = "INVALID_INPUT";
    private static final int HTTP_STATUS = HttpStatus.BAD_REQUEST.value();

    @Schema(description = "Field name that has invalid input")
    private final String fieldName;

    @Schema(description = "Invalid value that was provided")
    private final transient Object invalidValue;

    public InvalidInputException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
        this.fieldName = null;
        this.invalidValue = null;
    }

    public InvalidInputException(String fieldName, Object invalidValue, String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    public InvalidInputException(String message, Throwable cause) {
        super(ERROR_CODE, message, HTTP_STATUS, cause);
        this.fieldName = null;
        this.invalidValue = null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}
