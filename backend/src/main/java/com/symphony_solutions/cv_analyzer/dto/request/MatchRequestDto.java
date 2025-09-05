package com.symphony_solutions.cv_analyzer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for matching candidates to a vacancy description.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestDto {

    @NotBlank(message = "Vacancy description cannot be blank")
    @Size(min = 10, max = 10000, message = "Vacancy description must be between 10 and 10000 characters")
    private String vacancyDescription;
    
    // Optionally, add more fields if needed
}