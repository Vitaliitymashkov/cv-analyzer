package com.symphony_solutions.cv_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a prompt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptUpdateRequest {
    
    /**
     * The prompt type (e.g., "summary", "rating")
     */
    private String type;
    
    /**
     * The prompt role (e.g., "system", "user")
     */
    private String role;
    
    /**
     * The new prompt content
     */
    private String content;
}
