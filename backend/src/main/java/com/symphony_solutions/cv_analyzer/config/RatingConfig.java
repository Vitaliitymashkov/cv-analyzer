package com.symphony_solutions.cv_analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "candidate.rating")
public class RatingConfig {
    
    private int min = 1;
    private int max = 10;
    
    public int getMin() {
        return min;
    }
    
    public void setMin(int min) {
        this.min = min;
    }
    
    public int getMax() {
        return max;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    public int getRange() {
        return max - min + 1;
    }
    
    public String getRangeDescription() {
        return min + " to " + max;
    }
}
