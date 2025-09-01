package com.symphony_solutions.cv_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for a candidate summary with rating and fit explanation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSummary {
    private String name;
    private String filename;
    private String summary;
    private int rating;
} 