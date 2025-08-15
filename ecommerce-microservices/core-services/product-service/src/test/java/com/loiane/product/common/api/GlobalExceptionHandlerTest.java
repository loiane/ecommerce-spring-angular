package com.loiane.product.common.api;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("Entity Not Found Exception Handling")
    class EntityNotFoundExceptionTests {

        @Test
        @DisplayName("Should handle EntityNotFoundException with 404 status")
        void shouldHandleEntityNotFoundExceptionWith404Status() {
            // Given
            var exception = new EntityNotFoundException("Product not found with id: 123");

            // When
            var response = globalExceptionHandler.handleNotFound(exception);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertEquals("NOT_FOUND", body.get("code"));
            assertEquals("Product not found with id: 123", body.get("message"));
            assertNotNull(body.get("timestamp"));
            assertTrue(body.get("timestamp") instanceof OffsetDateTime);
        }

        @Test
        @DisplayName("Should handle EntityNotFoundException with null message")
        void shouldHandleEntityNotFoundExceptionWithNullMessage() {
            // Given
            var exception = new EntityNotFoundException();

            // When
            var response = globalExceptionHandler.handleNotFound(exception);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertEquals("NOT_FOUND", body.get("code"));
            assertNull(body.get("message"));
        }
    }

    @Nested
    @DisplayName("Data Integrity Violation Exception Handling")
    class DataIntegrityViolationExceptionTests {

        @Test
        @DisplayName("Should handle DataIntegrityViolationException with 409 status")
        void shouldHandleDataIntegrityViolationExceptionWith409Status() {
            // Given
            var rootCause = new SQLException("Duplicate entry 'test' for key 'unique_constraint'");
            var exception = new DataIntegrityViolationException("Constraint violation", rootCause);

            // When
            var response = globalExceptionHandler.handleConflict(exception);

            // Then
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertEquals("DATA_INTEGRITY_VIOLATION", body.get("code"));
            assertTrue(body.get("message").toString().contains("Constraint violation"));
            assertTrue(body.get("message").toString().contains("Duplicate entry 'test' for key 'unique_constraint'"));
            assertNotNull(body.get("timestamp"));
        }

        @Test
        @DisplayName("Should handle DataIntegrityViolationException without root cause")
        void shouldHandleDataIntegrityViolationExceptionWithoutRootCause() {
            // Given
            var exception = new DataIntegrityViolationException("Direct constraint violation");

            // When
            var response = globalExceptionHandler.handleConflict(exception);

            // Then
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertEquals("DATA_INTEGRITY_VIOLATION", body.get("code"));
            assertTrue(body.get("message").toString().contains("Direct constraint violation"));
        }

        @Test
        @DisplayName("Should handle nested exception causes")
        void shouldHandleNestedExceptionCauses() {
            // Given
            var deepCause = new SQLException("Deep root cause");
            var middleCause = new RuntimeException("Middle cause", deepCause);
            var exception = new DataIntegrityViolationException("Top level", middleCause);

            // When
            var response = globalExceptionHandler.handleConflict(exception);

            // Then
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertTrue(body.get("message").toString().contains("Deep root cause"));
        }
    }

    @Nested
    @DisplayName("Method Argument Not Valid Exception Handling")
    class MethodArgumentNotValidExceptionTests {

        @Test
        @DisplayName("Should handle MethodArgumentNotValidException with field errors")
        void shouldHandleMethodArgumentNotValidExceptionWithFieldErrors() {
            // Given
            var fieldError1 = new FieldError("product", "name", "Name is required");
            var fieldError2 = new FieldError("product", "price", "Price must be positive");

            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

            var exception = new MethodArgumentNotValidException(null, bindingResult);
            var headers = new HttpHeaders();
            var status = HttpStatus.BAD_REQUEST;

            // When
            var response = globalExceptionHandler.handleMethodArgumentNotValid(
                exception, headers, status, webRequest
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertEquals("VALIDATION_ERROR", body.get("code"));
            assertEquals("Validation failed", body.get("message"));
            assertNotNull(body.get("timestamp"));

            @SuppressWarnings("unchecked")
            var errors = (Map<String, String>) body.get("errors");
            assertNotNull(errors);
            assertEquals(2, errors.size());
            assertEquals("Name is required", errors.get("name"));
            assertEquals("Price must be positive", errors.get("price"));
        }

        @Test
        @DisplayName("Should handle MethodArgumentNotValidException with object errors")
        void shouldHandleMethodArgumentNotValidExceptionWithObjectErrors() {
            // Given
            var objectError = new ObjectError("product", "Invalid product data");

            when(bindingResult.getAllErrors()).thenReturn(List.of(objectError));

            var exception = new MethodArgumentNotValidException(null, bindingResult);
            var headers = new HttpHeaders();
            var status = HttpStatus.BAD_REQUEST;

            // When
            var response = globalExceptionHandler.handleMethodArgumentNotValid(
                exception, headers, status, webRequest
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);

            @SuppressWarnings("unchecked")
            var errors = (Map<String, String>) body.get("errors");
            assertNotNull(errors);
            assertEquals(1, errors.size());
            assertEquals("Invalid product data", errors.get("product"));
        }

        @Test
        @DisplayName("Should handle MethodArgumentNotValidException with mixed errors")
        void shouldHandleMethodArgumentNotValidExceptionWithMixedErrors() {
            // Given
            var fieldError = new FieldError("product", "name", "Name is required");
            var objectError = new ObjectError("product", "Overall validation failed");

            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError, objectError));

            var exception = new MethodArgumentNotValidException(null, bindingResult);
            var headers = new HttpHeaders();
            var status = HttpStatus.BAD_REQUEST;

            // When
            var response = globalExceptionHandler.handleMethodArgumentNotValid(
                exception, headers, status, webRequest
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);

            @SuppressWarnings("unchecked")
            var errors = (Map<String, String>) body.get("errors");
            assertNotNull(errors);
            assertEquals(2, errors.size());
            assertEquals("Name is required", errors.get("name"));
            assertEquals("Overall validation failed", errors.get("product"));
        }

        @Test
        @DisplayName("Should handle MethodArgumentNotValidException with no errors")
        void shouldHandleMethodArgumentNotValidExceptionWithNoErrors() {
            // Given
            when(bindingResult.getAllErrors()).thenReturn(List.of());

            var exception = new MethodArgumentNotValidException(null, bindingResult);
            var headers = new HttpHeaders();
            var status = HttpStatus.BAD_REQUEST;

            // When
            var response = globalExceptionHandler.handleMethodArgumentNotValid(
                exception, headers, status, webRequest
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertEquals("VALIDATION_ERROR", body.get("code"));
            assertEquals("Validation failed", body.get("message"));

            @SuppressWarnings("unchecked")
            var errors = (Map<String, String>) body.get("errors");
            assertNotNull(errors);
            assertTrue(errors.isEmpty());
        }
    }

    @Nested
    @DisplayName("Error Body Creation Tests")
    class ErrorBodyTests {

        @Test
        @DisplayName("Should create error body with all required fields")
        void shouldCreateErrorBodyWithAllRequiredFields() {
            // Given
            var exception = new EntityNotFoundException("Test message");

            // When
            var response = globalExceptionHandler.handleNotFound(exception);

            // Then
            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertNotNull(body);
            assertEquals(3, body.size());
            assertTrue(body.containsKey("timestamp"));
            assertTrue(body.containsKey("code"));
            assertTrue(body.containsKey("message"));
        }

        @Test
        @DisplayName("Should generate timestamp for error responses")
        void shouldGenerateTimestampForErrorResponses() {
            // Given
            var exception = new EntityNotFoundException("Test");
            var beforeCall = OffsetDateTime.now();

            // When
            var response = globalExceptionHandler.handleNotFound(exception);

            // Then
            var afterCall = OffsetDateTime.now();

            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            var timestamp = (OffsetDateTime) body.get("timestamp");

            assertNotNull(timestamp);
            assertTrue(timestamp.isAfter(beforeCall.minusSeconds(1))); // Allow some tolerance
            assertTrue(timestamp.isBefore(afterCall.plusSeconds(1))); // Allow some tolerance
        }
    }

    @Nested
    @DisplayName("Root Message Extraction Tests")
    class RootMessageExtractionTests {

        @Test
        @DisplayName("Should extract root message from single level exception")
        void shouldExtractRootMessageFromSingleLevelException() {
            // Given
            var singleException = new SQLException("Direct message");
            var wrapperException = new DataIntegrityViolationException("Wrapper", singleException);

            // When
            var response = globalExceptionHandler.handleConflict(wrapperException);

            // Then
            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertTrue(body.get("message").toString().contains("Direct message"));
        }

        @Test
        @DisplayName("Should extract root message from multi-level exception chain")
        void shouldExtractRootMessageFromMultiLevelExceptionChain() {
            // Given
            var rootException = new SQLException("Root cause message");
            var level2Exception = new RuntimeException("Level 2", rootException);
            var level3Exception = new IllegalStateException("Level 3", level2Exception);
            var wrapperException = new DataIntegrityViolationException("Top level", level3Exception);

            // When
            var response = globalExceptionHandler.handleConflict(wrapperException);

            // Then
            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertTrue(body.get("message").toString().contains("Root cause message"));
        }

        @Test
        @DisplayName("Should handle exception without cause")
        void shouldHandleExceptionWithoutCause() {
            // Given
            var directException = new DataIntegrityViolationException("Direct message without cause");

            // When
            var response = globalExceptionHandler.handleConflict(directException);

            // Then
            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>) response.getBody();
            assertTrue(body.get("message").toString().contains("Direct message without cause"));
        }
    }
}
