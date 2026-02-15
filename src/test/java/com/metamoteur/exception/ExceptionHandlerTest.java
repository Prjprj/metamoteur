package com.metamoteur.exception;

import com.metamoteur.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/search");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void handleInvalidSearchQuery_ShouldReturnBadRequest() {
        InvalidSearchQueryException ex = InvalidSearchQueryException.emptyQuery();

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleInvalidSearchQuery(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_SEARCH_QUERY", response.getBody().getErrorCode());
    }

    @Test
    void handleResourceNotFound_ShouldReturnNotFound() {
        ResourceNotFoundException ex = ResourceNotFoundException.search(123L);

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleResourceNotFound(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void handleRateLimitExceeded_ShouldReturnTooManyRequests() {
        RateLimitExceededException ex = new RateLimitExceededException(20, "minute");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleRateLimitExceeded(ex, webRequest);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Retry-After"));
    }

    @Test
    void handleValidationException_ShouldIncludeFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("searchRequest", "query", "must not be blank");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidationException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getValidationErrors());
        assertTrue(response.getBody().getValidationErrors().containsKey("query"));
    }
}