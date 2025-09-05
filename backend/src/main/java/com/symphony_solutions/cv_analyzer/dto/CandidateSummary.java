package com.symphony_solutions.cv_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSummary {
    private String name;
    private String filename;
    private String summary;
    private int rating;
    private int minRating;
    private int maxRating;
}
