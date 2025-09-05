package com.symphony_solutions.cv_analyzer.controller;

import com.symphony_solutions.cv_analyzer.dto.response.PricingInfoResponseDto;
import com.symphony_solutions.cv_analyzer.service.CostCalculationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for cost-related metrics and information.
 */
@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class CostController {
    
    private final CostCalculationService costCalculationService;
    
    /**
     * Get current cost metrics including total cost, token usage, and pricing information.
     */
    @GetMapping("/metrics")
    public Map<String, Object> getCostMetrics() {
        return Map.of(
            "totalCost", costCalculationService.getTotalCost(),
            "totalInputTokens", costCalculationService.getTotalInputTokens(),
            "totalOutputTokens", costCalculationService.getTotalOutputTokens(),
            "pricing", costCalculationService.getPricingInfo(),
            "latestAiCall", costCalculationService.getLatestAiCall()
        );
    }
    
    /**
     * Get pricing information only.
     */
    @GetMapping("/pricing")
    public PricingInfoResponseDto getPricing() {
        return costCalculationService.getPricingInfo();
    }
}
