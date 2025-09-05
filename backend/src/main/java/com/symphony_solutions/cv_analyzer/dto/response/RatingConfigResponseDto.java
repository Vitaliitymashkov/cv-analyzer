package com.symphony_solutions.cv_analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for rating configuration.
 * Contains the system-wide rating range information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingConfigResponseDto {
    private int minRating;
    private int maxRating;
    private String rangeDescription;
}
