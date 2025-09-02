package com.symphony_solutions.cv_analyzer.exception;

/**
 * Exception thrown when CV parsing fails.
 */
public class CvParsingException extends RuntimeException {
    
    public CvParsingException(String message) {
        super(message);
    }
    
    public CvParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
