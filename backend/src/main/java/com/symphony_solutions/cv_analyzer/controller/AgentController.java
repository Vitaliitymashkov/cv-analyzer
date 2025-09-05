package com.symphony_solutions.cv_analyzer.controller;

import com.symphony_solutions.cv_analyzer.model.Resume;
import com.symphony_solutions.cv_analyzer.service.ResumeService;
import com.symphony_solutions.cv_analyzer.service.AgentSummaryService;
import com.symphony_solutions.cv_analyzer.dto.request.MatchRequestDto;
import com.symphony_solutions.cv_analyzer.dto.response.CandidateSummaryResponseDto;
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

  private final ResumeService resumeService;

  private final AgentSummaryService agentSummaryService;

  /**
   * Returns the most relevant candidates for a given vacancy description, with LLM-generated summary and rating.
   *
   * @param request the vacancy description
   * @return list of candidate summaries with individual ratings
   */
  @PostMapping("/match")
  public List<CandidateSummaryResponseDto> matchCvs(@Valid @RequestBody MatchRequestDto request) {
    log.info("Processing candidate match request for vacancy: {}",
        request.getVacancyDescription().substring(0, Math.min(100, request.getVacancyDescription().length())));

    try {
      List<Resume> topResumes = resumeService.findTopCandidates(request.getVacancyDescription(), 5);
      List<CandidateSummaryResponseDto> summaries = new ArrayList<>();

      for (Resume resume : topResumes) {
        try {
          log.debug("Processing CV: {}", resume.getFilename());
          String summary = agentSummaryService.generateSummary(request.getVacancyDescription(), resume.getContent());
          int rating = agentSummaryService.generateRating(request.getVacancyDescription(), resume.getContent());

          CandidateSummaryResponseDto candidate = CandidateSummaryResponseDto.builder()
              .name(resume.getName())
              .filename(resume.getFilename())
              .summary(summary)
              .rating(rating)
              .build();
          summaries.add(candidate);
        } catch (NonTransientAiException e) {
          log.error("AI service error processing CV: {}", resume.getFilename(), e);
          // Re-throw AI exceptions so they can be handled by GlobalExceptionHandler
          throw e;
        } catch (Exception e) {
          log.error("Failed to process CV: {}", resume.getFilename(), e);
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
