package com.symphony_solutions.cv_analyzer.controller;

import com.symphony_solutions.cv_analyzer.config.RatingConfig;
import com.symphony_solutions.cv_analyzer.model.Cv;
import com.symphony_solutions.cv_analyzer.service.CvService;
import com.symphony_solutions.cv_analyzer.service.AgentSummaryService;
import com.symphony_solutions.cv_analyzer.dto.MatchRequest;
import com.symphony_solutions.cv_analyzer.dto.CandidateSummary;
import com.symphony_solutions.cv_analyzer.exception.CvParsingException;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.ArrayList;
import jakarta.validation.Valid;

/**
 * REST controller for matching candidates to a job vacancy.
 */
@Slf4j
@RestController
@RequestMapping("/api/candidate-matcher")
@RequiredArgsConstructor
@Validated
public class AgentController {
    private final CvService cvService;
    private final AgentSummaryService agentSummaryService;
    private final RatingConfig ratingConfig;

    /**
     * Returns the most relevant candidates for a given vacancy description, with LLM-generated summary and rating.
     * @param request the vacancy description
     * @return list of candidate summaries with rating range information
     */
    @PostMapping("/match")
    public List<CandidateSummary> matchCvs(@Valid @RequestBody MatchRequest request) {
        log.info("Processing candidate match request for vacancy: {}", 
                request.getVacancyDescription().substring(0, Math.min(100, request.getVacancyDescription().length())));
        
        try {
            List<Cv> topCvs = cvService.findTopCandidates(request.getVacancyDescription(), 5);
            List<CandidateSummary> summaries = new ArrayList<>();
            
            for (Cv cv : topCvs) {
                try {
                    log.debug("Processing CV: {}", cv.getFilename());
                    String summary = agentSummaryService.generateSummary(request.getVacancyDescription(), cv.getContent());
                    int rating = agentSummaryService.generateRating(request.getVacancyDescription(), cv.getContent());
                    
                    CandidateSummary candidate = new CandidateSummary(
                        cv.getName(),
                        cv.getFilename(),
                        summary,
                        rating,
                        ratingConfig.getMin(),
                        ratingConfig.getMax()
                    );
                    summaries.add(candidate);
                } catch (NonTransientAiException e) {
                    log.error("AI service error processing CV: {}", cv.getFilename(), e);
                    // Re-throw AI exceptions so they can be handled by GlobalExceptionHandler
                    throw e;
                } catch (Exception e) {
                    log.error("Failed to process CV: {}", cv.getFilename(), e);
                    // Continue with other CVs for non-AI errors
                    // Could add a fallback candidate with error message
                }
            }
            
            log.info("Successfully processed {} candidates", summaries.size());
            return summaries;
        } catch (Exception e) {
            log.error("Error in candidate matching process", e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
}
