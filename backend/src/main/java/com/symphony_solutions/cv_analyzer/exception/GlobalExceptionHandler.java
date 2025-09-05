package com.symphony_solutions.cv_analyzer.exception;

import com.symphony_solutions.cv_analyzer.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Simple global exception handler for the CV Analyzer application.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle AI service errors (rate limiting, authentication, etc.)
     */
    @ExceptionHandler(NonTransientAiException.class)
    public ResponseEntity<ErrorResponse> handleAiServiceError(NonTransientAiException ex) {
        log.error("AI service error: {}", ex.getMessage());
        
        String message = ex.getMessage();
        
        // Check for specific error types based on the message content
        if (message.contains("Rate limit") || message.contains("429")) {
            return ResponseEntity.status(429)
                    .body(ErrorResponse.of(429, "Too Many Requests", "AI service rate limit exceeded. Please try again later."));
        } else if (message.contains("Invalid API Key") || message.contains("401") || message.contains("invalid_api_key")) {
            return ResponseEntity.status(401)
                    .body(ErrorResponse.of(401, "Unauthorized", "Invalid API key. Please check your configuration."));
        } else if (message.contains("403") || message.contains("Forbidden")) {
            return ResponseEntity.status(403)
                    .body(ErrorResponse.of(403, "Forbidden", "Access denied. Please check your API permissions."));
        } else if (message.contains("404") || message.contains("Not Found")) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of(404, "Not Found", "AI service endpoint not found."));
        } else if (message.contains("500") || message.contains("Internal Server Error")) {
            return ResponseEntity.status(502)
                    .body(ErrorResponse.of(502, "Bad Gateway", "AI service is temporarily unavailable. Please try again later."));
        } else {
            // Generic AI service error
            return ResponseEntity.status(502)
                    .body(ErrorResponse.of(502, "Bad Gateway", "AI service error: " + message));
        }
    }

    /**
     * Handle CV parsing exceptions
     */
    @ExceptionHandler(CvParsingException.class)
    public ResponseEntity<ErrorResponse> handleCvParsingException(CvParsingException ex) {
        log.error("CV parsing error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Bad Request", "Failed to process CV file: " + ex.getMessage()));
    }

    /**
     * Handle prompt management exceptions
     */
    @ExceptionHandler(PromptManagementException.class)
    public ResponseEntity<ErrorResponse> handlePromptManagementException(PromptManagementException ex) {
        log.error("Prompt management error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Bad Request", "Prompt management error: " + ex.getMessage()));
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        // Extract user-friendly validation messages
        StringBuilder errorMessage = new StringBuilder("Validation failed");
        
        if (ex.getBindingResult().hasFieldErrors()) {
            errorMessage.append(": ");
            ex.getBindingResult().getFieldErrors().forEach(error -> {
                if (errorMessage.length() > "Validation failed: ".length()) {
                    errorMessage.append("; ");
                }
                errorMessage.append(error.getDefaultMessage());
            });
        }
        
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Bad Request", errorMessage.toString()));
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Bad Request", "Invalid request: " + ex.getMessage()));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(ErrorResponse.of(500, "Internal Server Error", "An unexpected error occurred. Please try again later."));
    }
}
