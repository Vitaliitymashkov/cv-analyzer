package com.symphony_solutions.cv_analyzer.exception;

/**
 * Exception thrown when CV parsing fails.
 */
public class ResumeParsingException extends RuntimeException {
    
    public ResumeParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
