package com.symphony_solutions.cv_analyzer.dto.request;

import com.symphony_solutions.cv_analyzer.dto.type.PromptRole;
import com.symphony_solutions.cv_analyzer.dto.type.PromptType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a prompt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptUpdateRequestDto {
    
    /**
     * The prompt type (e.g., SUMMARY, RATING)
     */
    private PromptType type;

    /**
     * The prompt role (e.g., SYSTEM, USER)
     */
    private PromptRole role;

    /**
     * The new prompt content
     */
    private String content;
}
