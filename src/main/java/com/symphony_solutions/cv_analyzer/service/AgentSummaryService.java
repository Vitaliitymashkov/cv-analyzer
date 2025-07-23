package com.symphony_solutions.cv_analyzer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentSummaryService {
    private final ChatClient.Builder chatClient;

    private static final String SYSTEM_PROMPT = "You are an expert recruiter. Given a job description and a candidate's CV, explain in 2-3 sentences why this candidate is a good fit for the job. Be specific about relevant skills and experience.";
    private static final String RATING_PROMPT = "On a scale from 1 to 12 (where 12 is the best fit), rate how well this candidate matches the job description. Only return the number.";

    public String generateSummary(String vacancyDescription, String cvContent) {
        String userPrompt = String.format(
            "Job Description:\n%s\n\nCandidate CV:\n%s\n\n%s",
            vacancyDescription,
            cvContent,
            "Explain concisely why this candidate is a good fit."
        );
        return chatClient.build().prompt(SYSTEM_PROMPT + "\n" + userPrompt).call().content();
    }

    public int generateRating(String vacancyDescription, String cvContent) {
        String userPrompt = String.format(
            "Job Description:\n%s\n\nCandidate CV:\n%s\n\n%s",
            vacancyDescription,
            cvContent,
            RATING_PROMPT
        );
        String response = chatClient.build().prompt(userPrompt).call().content();
        try {
            return Integer.parseInt(response.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
            return 1; // fallback to lowest rating
        }
    }
}