package com.symphony_solutions.cv_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal model for chat responses that includes both content and token usage statistics.
 * This eliminates the need for thread-local storage and provides a clean way to pass
 * token usage information to AOP aspects.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalChatResponse {
    
    /**
     * The actual response content (text)
     */
    private String content;
    
    /**
     * Number of input/prompt tokens used
     */
    private int inputTokens;
    
    /**
     * Number of output/completion tokens used
     */
    private int outputTokens;
}
