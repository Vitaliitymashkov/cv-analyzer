package com.symphony_solutions.cv_analyzer.aspect;

import com.symphony_solutions.cv_analyzer.model.InternalChatResponse;
import com.symphony_solutions.cv_analyzer.service.AgentSummaryService;
import com.symphony_solutions.cv_analyzer.service.CostCalculationService;
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
    
    // Constants for fallback estimates
    private static final int ESTIMATED_INPUT_TOKENS_SUMMARY = 500;
    private static final int ESTIMATED_OUTPUT_TOKENS_SUMMARY = 100;
    private static final int ESTIMATED_INPUT_TOKENS_RATING = 500;
    private static final int ESTIMATED_OUTPUT_TOKENS_RATING = 10;

    @Around("execution(* com.symphony_solutions.cv_analyzer.service.AgentSummaryService.generateSummary(..)) || " +
            "execution(* com.symphony_solutions.cv_analyzer.service.AgentSummaryService.generateRating(..))")
    public Object trackCostAndTokens(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        
        String methodName = joinPoint.getSignature().getName();
        InternalChatResponse internalResponse = getInternalResponse(joinPoint, methodName);
        
        if (internalResponse != null) {
            recordActualCost(methodName, internalResponse);
        } else {
            recordEstimatedCost(methodName);
        }
        
        return result;
    }
    
    private InternalChatResponse getInternalResponse(ProceedingJoinPoint joinPoint, String methodName) {
        AgentSummaryService service = (AgentSummaryService) joinPoint.getTarget();
        Object[] args = joinPoint.getArgs();
        String vacancyDescription = (String) args[0];
        String cvContent = (String) args[1];
        
        return switch (methodName) {
            case "generateSummary" -> service.generateSummaryInternal(vacancyDescription, cvContent);
            case "generateRating" -> service.generateRatingInternal(vacancyDescription, cvContent);
            default -> null;
        };
    }
    
    private void recordActualCost(String methodName, InternalChatResponse response) {
        costCalculationService.calculateAndRecordCost(
            response.getInputTokens(),
            response.getOutputTokens()
        );
        
        log.info("[Cost Tracker]: Cost tracked for {} - {} input, {} output tokens",
            methodName, response.getInputTokens(), response.getOutputTokens());
    }
    
    private void recordEstimatedCost(String methodName) {
      if (methodName.equals("generateSummary")) {
        costCalculationService.calculateAndRecordCost(
            ESTIMATED_INPUT_TOKENS_SUMMARY,
            ESTIMATED_OUTPUT_TOKENS_SUMMARY
        );
        log.info("[Cost Tracker]: Cost tracked for generateSummary (estimated) - {} input, {} output tokens",
            ESTIMATED_INPUT_TOKENS_SUMMARY, ESTIMATED_OUTPUT_TOKENS_SUMMARY);
      } else if (methodName.equals("generateRating")) {
        costCalculationService.calculateAndRecordCost(
            ESTIMATED_INPUT_TOKENS_RATING,
            ESTIMATED_OUTPUT_TOKENS_RATING
        );
        log.info("[Cost Tracker]: Cost tracked for generateRating (estimated) - {} input, {} output tokens",
            ESTIMATED_INPUT_TOKENS_RATING, ESTIMATED_OUTPUT_TOKENS_RATING);
      }
    }
}