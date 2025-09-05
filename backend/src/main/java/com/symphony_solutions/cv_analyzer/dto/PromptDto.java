package com.symphony_solutions.cv_analyzer.dto;

import com.symphony_solutions.cv_analyzer.dto.type.PromptRole;
import com.symphony_solutions.cv_analyzer.dto.type.PromptType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for prompt information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptDto {
    
    /**
     * The prompt type (e.g., SUMMARY, RATING)
     */
    private PromptType type;

    /**
     * The prompt role (e.g., SYSTEM, USER)
     */
    private PromptRole role;

    /**
     * The prompt content
     */
    private String content;
    
    /**
     * The file path where this prompt is stored
     */
    private String filePath;
    
    /**
     * Whether the prompt is currently cached in memory
     */
    private boolean cached;
}
