package com.loiane.product.common.api;

import com.loiane.product.common.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("Should handle BusinessException with proper status and message")
    void shouldHandleBusinessExceptionCorrectly() {
        // Given
        var exception = new ProductNotFoundException("123e4567-e89b-12d3-a456-426614174000");

        // When
        var response = globalExceptionHandler.handleBusinessException(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals("PRODUCT_NOT_FOUND", body.code());
        assertTrue(body.message().contains("Product not found with SKU: 123e4567-e89b-12d3-a456-426614174000"));
        assertEquals("/api/test", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    @DisplayName("Should return proper HTTP status for business exceptions")
    void shouldReturnCorrectHttpStatus() {
        // Given
        var exception = new ProductNotFoundException("test-id");

        // When
        var response = globalExceptionHandler.handleBusinessException(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
