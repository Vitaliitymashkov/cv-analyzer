package com.symphony_solutions.cv_analyzer.aspect;

import com.symphony_solutions.cv_analyzer.model.InternalChatResponse;
import com.symphony_solutions.cv_analyzer.service.CostCalculationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CostTrackingAspect {

  private final CostCalculationService costCalculationService;

  @Around("execution(* com.symphony_solutions.cv_analyzer.service.AgentSummaryService.generateSummary(..)) || " +
      "execution(* com.symphony_solutions.cv_analyzer.service.AgentSummaryService.generateRating(..))")
  public Object trackCostAndTokens(ProceedingJoinPoint joinPoint) throws Throwable {
    Object result = joinPoint.proceed();
    getInternalResponse(result)
        .ifPresent(response -> recordActualCost(joinPoint.getSignature().getName(), response));
    return result;
  }

  private Optional<InternalChatResponse> getInternalResponse(Object result) {
    if (result instanceof InternalChatResponse response) {
      return Optional.of(response);
    }
    return Optional.empty();
  }

  private void recordActualCost(String methodName, InternalChatResponse response) {
    costCalculationService.calculateAndRecordCost(
        response.getInputTokens(),
        response.getOutputTokens()
    );

    log.info("[Cost Tracker]: Cost tracked for {} - {} input, {} output tokens",
        methodName, response.getInputTokens(), response.getOutputTokens());
  }
}