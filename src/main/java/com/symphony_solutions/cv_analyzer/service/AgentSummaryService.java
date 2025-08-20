package com.symphony_solutions.cv_analyzer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentSummaryService {

    private final ChatClient.Builder chatClient;

    private final PromptService promptService;

    public String generateSummary(String vacancyDescription, String cvContent) {
        String system = promptService.getSummarySystemPrompt();
        String userTemplate = promptService.getSummaryUserPrompt();
        String userMessage = userTemplate
                .replace("{{vacancy_description}}", vacancyDescription)
                .replace("{{cv_content}}", cvContent);

        return chatClient
                .build()
                .prompt()
                .system(system)
                .user(userMessage)
                .call()
                .content();
    }

    public int generateRating(String vacancyDescription, String cvContent) {
        String system = promptService.getRatingSystemPrompt();
        String userTemplate = promptService.getRatingUserPrompt();
        String userMessage = userTemplate
                .replace("{{vacancy_description}}", vacancyDescription)
                .replace("{{cv_content}}", cvContent);

        String response = chatClient
                .build()
                .prompt()
                .system(system)
                .user(userMessage)
                .call()
                .content();
        try {
            return Integer.parseInt(response.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
            return 1; // fallback to lowest rating
        }
    }
}