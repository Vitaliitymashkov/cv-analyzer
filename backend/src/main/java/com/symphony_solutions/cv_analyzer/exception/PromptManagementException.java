package com.symphony_solutions.cv_analyzer.exception;

/**
 * Exception thrown when prompt management operations fail.
 */
public class PromptManagementException extends RuntimeException {
    
    public PromptManagementException(String message) {
        super(message);
    }
    
    public PromptManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
