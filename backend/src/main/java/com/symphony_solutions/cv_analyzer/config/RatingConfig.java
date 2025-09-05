package com.symphony_solutions.cv_analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "candidate.rating")
public class RatingConfig {
    
    private int min = 1;
    private int max = 10;
    
    public String getRangeDescription() {
        return min + " to " + max;
    }
}
