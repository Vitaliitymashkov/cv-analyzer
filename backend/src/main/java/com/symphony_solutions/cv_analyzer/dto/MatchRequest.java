package com.symphony_solutions.cv_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for matching candidates to a vacancy description.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {
    private String vacancyDescription;
    // Optionally, add more fields if needed
}