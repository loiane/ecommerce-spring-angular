package com.loiane.product.common.api;

import com.loiane.product.common.api.dto.ErrorResponse;
import com.loiane.product.common.api.dto.ValidationErrorResponse;
import com.loiane.product.common.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for all REST API exceptions.
 * Provides consistent error responses and proper logging for all error scenarios.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle custom business exceptions.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        String path = request.getRequestURI();

        log.warn("Business exception occurred [correlationId={}] [path={}] [code={}]: {}",
                correlationId, path, ex.getErrorCode(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                ex.getHttpStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                path
        );

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handle JPA EntityNotFoundException for backward compatibility.
     * @deprecated Use custom business exceptions instead
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @Deprecated(since = "1.0.0", forRemoval = true)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        String path = request.getRequestURI();

        log.warn("Entity not found [correlationId={}] [path={}]: {}",
                correlationId, path, ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                path
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle data integrity violations (unique constraints, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        String path = request.getRequestURI();
        String rootMessage = rootMessage(ex);

        log.error("Data integrity violation [correlationId={}] [path={}]: {}",
                correlationId, path, rootMessage, ex);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "DATA_INTEGRITY_VIOLATION",
                "Constraint violation: " + rootMessage,
                path
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        String path = request.getRequestURI();

        log.warn("Illegal argument [correlationId={}] [path={}]: {}",
                correlationId, path, ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                path
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle database access exceptions
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(
            DataAccessException ex, HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        String path = request.getRequestURI();

        log.error("Database access error [correlationId={}] [path={}]: {}",
                correlationId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "DATABASE_ERROR",
                "A database error occurred. Please try again later.",
                path
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        String correlationId = generateCorrelationId();
        String path = request.getRequestURI();

        log.error("Unexpected error [correlationId={}] [path={}]: {}",
                correlationId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                path
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        String correlationId = generateCorrelationId();
        String path = extractPath(request);

        Map<String, String> fieldErrors = new HashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            String field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            fieldErrors.put(field, error.getDefaultMessage());
        }

        log.warn("Validation failed [correlationId={}] [path={}] [errors={}]",
                correlationId, path, fieldErrors);

        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                status.value(),
                "VALIDATION_ERROR",
                "Input validation failed",
                path,
                fieldErrors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private String generateCorrelationId() {
        long timestamp = System.currentTimeMillis();
        long threadId = Thread.currentThread().getId();
        String uuidPart = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%d-%d-%s", timestamp, threadId, uuidPart);
    }

    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }
        return null;
    }

    private String rootMessage(Throwable t) {
        Throwable current = t;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage();
    }
}
