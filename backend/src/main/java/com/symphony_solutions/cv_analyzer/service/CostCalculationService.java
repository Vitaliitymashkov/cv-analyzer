package com.symphony_solutions.cv_analyzer.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for calculating and tracking OpenAI API costs based on token usage.
 */
@Service
public class CostCalculationService {
    
    private final MeterRegistry meterRegistry;
    
    @Value("${openai.pricing.input-tokens-per-million:2.50}")
    private BigDecimal inputTokensPerMillion;
    
    @Value("${openai.pricing.output-tokens-per-million:10.00}")
    private BigDecimal outputTokensPerMillion;
    
    @Value("${openai.pricing.currency:USD}")
    private String currency;
    
    private Counter totalCostCounter;
    private Counter inputTokensCounter;
    private Counter outputTokensCounter;
    
    // Latest AI call tracking
    private volatile LatestAiCall latestAiCall;
    
    public CostCalculationService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @PostConstruct
    public void init() {
        this.totalCostCounter = Counter.builder("gen_ai.client.cost.total")
                .description("Total cost of GenAI API calls")
                .baseUnit(currency)
                .register(meterRegistry);
                
        this.inputTokensCounter = Counter.builder("gen_ai.client.tokens.input")
                .description("Total input tokens consumed")
                .baseUnit("tokens")
                .register(meterRegistry);
                
        this.outputTokensCounter = Counter.builder("gen_ai.client.tokens.output")
                .description("Total output tokens consumed")
                .baseUnit("tokens")
                .register(meterRegistry);
    }
    
    /**
     * Calculate and record the cost for a given number of input and output tokens.
     * 
     * @param inputTokens Number of input tokens
     * @param outputTokens Number of output tokens
     * @return Total cost for this operation
     */
    public BigDecimal calculateAndRecordCost(int inputTokens, int outputTokens) {
        BigDecimal inputCost = calculateInputCost(inputTokens);
        BigDecimal outputCost = calculateOutputCost(outputTokens);
        BigDecimal totalCost = inputCost.add(outputCost);
        
        // Record the metrics
        totalCostCounter.increment(totalCost.doubleValue());
        inputTokensCounter.increment(inputTokens);
        outputTokensCounter.increment(outputTokens);
        
        // Track the latest AI call
        this.latestAiCall = new LatestAiCall(
            LocalDateTime.now(),
            inputTokens,
            outputTokens,
            totalCost,
            inputCost,
            outputCost
        );
        
        return totalCost;
    }
    
    /**
     * Calculate cost for input tokens.
     */
    private BigDecimal calculateInputCost(int tokens) {
        if (tokens <= 0) return BigDecimal.ZERO;
        
        BigDecimal tokensInMillion = new BigDecimal(tokens).divide(new BigDecimal(1_000_000), 6, RoundingMode.HALF_UP);
        return tokensInMillion.multiply(inputTokensPerMillion).setScale(4, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate cost for output tokens.
     */
    private BigDecimal calculateOutputCost(int tokens) {
        if (tokens <= 0) return BigDecimal.ZERO;
        
        BigDecimal tokensInMillion = new BigDecimal(tokens).divide(new BigDecimal(1_000_000), 6, RoundingMode.HALF_UP);
        return tokensInMillion.multiply(outputTokensPerMillion).setScale(4, RoundingMode.HALF_UP);
    }
    
    /**
     * Get the current total cost from metrics.
     */
    public BigDecimal getTotalCost() {
        return BigDecimal.valueOf(totalCostCounter.count()).setScale(4, RoundingMode.HALF_UP);
    }
    
    /**
     * Get the current total input tokens from metrics.
     */
    public long getTotalInputTokens() {
        return (long) inputTokensCounter.count();
    }
    
    /**
     * Get the current total output tokens from metrics.
     */
    public long getTotalOutputTokens() {
        return (long) outputTokensCounter.count();
    }
    
    /**
     * Get pricing information.
     */
    public PricingInfo getPricingInfo() {
        return new PricingInfo(
            inputTokensPerMillion,
            outputTokensPerMillion,
            currency
        );
    }
    
    /**
     * Get information about the latest AI call.
     */
    public LatestAiCall getLatestAiCall() {
        return latestAiCall;
    }

  /**
   * Data class for pricing information.
   */
  public record PricingInfo(BigDecimal inputTokensPerMillion,
                            BigDecimal outputTokensPerMillion,
                            String currency) {
  }
  
  /**
   * Data class for latest AI call information.
   */
  public record LatestAiCall(LocalDateTime timestamp,
                            int inputTokens,
                            int outputTokens,
                            BigDecimal totalCost,
                            BigDecimal inputCost,
                            BigDecimal outputCost) {
  }
}
