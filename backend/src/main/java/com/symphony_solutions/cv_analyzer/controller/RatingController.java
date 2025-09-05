package com.symphony_solutions.cv_analyzer.controller;

import com.symphony_solutions.cv_analyzer.config.RatingConfig;
import com.symphony_solutions.cv_analyzer.dto.response.RatingConfigResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for providing rating configuration.
 * Returns the system-wide rating configuration that applies to all candidates.
 */
@Slf4j
@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingConfig ratingConfig;

    /**
     * Returns the current rating configuration (min and max values).
     * @return RatingConfigResponseDto containing min and max rating values
     */
    @GetMapping("/config")
    public RatingConfigResponseDto getRatingConfig() {
        log.debug("Providing rating configuration: min={}, max={}", 
                ratingConfig.getMin(), ratingConfig.getMax());
        
        return new RatingConfigResponseDto(
            ratingConfig.getMin(),
            ratingConfig.getMax(),
            ratingConfig.getRangeDescription()
        );
    }
}
