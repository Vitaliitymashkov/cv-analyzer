package com.symphony_solutions.cv_analyzer.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data class for pricing information.
 */
@Data
@AllArgsConstructor
public final class PricingInfoResponseDto {

  private final BigDecimal inputTokensPerMillion;

  private final BigDecimal outputTokensPerMillion;

  private final String currency;

}
