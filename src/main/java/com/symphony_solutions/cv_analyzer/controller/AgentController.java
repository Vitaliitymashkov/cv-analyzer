package com.symphony_solutions.cv_analyzer.controller;

import com.symphony_solutions.cv_analyzer.model.Cv;
import com.symphony_solutions.cv_analyzer.service.CvService;
import com.symphony_solutions.cv_analyzer.service.AgentSummaryService;
import com.symphony_solutions.cv_analyzer.dto.MatchRequest;
import com.symphony_solutions.cv_analyzer.dto.CandidateSummary;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;

/**
 * REST controller for matching candidates to a job vacancy.
 */
@RestController
@RequestMapping("/api/candidate-matcher")
@RequiredArgsConstructor
public class AgentController {
    private final CvService cvService;
    private final AgentSummaryService agentSummaryService;

    /**
     * Returns the most relevant candidates for a given vacancy description, with LLM-generated summary and rating.
     * @param request the vacancy description
     * @return list of candidate summaries
     */
    @PostMapping("/match")
    public List<CandidateSummary> matchCvs(@RequestBody MatchRequest request) {
        List<Cv> topCvs = cvService.findTopCandidates(request.getVacancyDescription(), 5);
        List<CandidateSummary> summaries = new ArrayList<>();
        for (Cv cv : topCvs) {
            String summary = agentSummaryService.generateSummary(request.getVacancyDescription(), cv.getContent());
            int rating = agentSummaryService.generateRating(request.getVacancyDescription(), cv.getContent());
            summaries.add(new CandidateSummary(cv.getName(), cv.getFilename(), summary, rating));
        }
        return summaries;
    }
}
