package com.symphony_solutions.cv_analyzer.service;

import com.symphony_solutions.cv_analyzer.model.InternalChatResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentSummaryService {

    private final ChatClient.Builder chatClient;

    private final PromptService promptService;

        public String generateSummary(String vacancyDescription, String cvContent) {
        return generateSummaryInternal(vacancyDescription, cvContent).getContent();
    }

    public InternalChatResponse generateSummaryInternal(String vacancyDescription, String cvContent) {
        return generateInternalResponse(
            promptService.getSummarySystemPrompt(),
            promptService.getSummaryUserPrompt(),
            vacancyDescription,
            cvContent
        );
    }

    public int generateRating(String vacancyDescription, String cvContent) {
        InternalChatResponse response = generateRatingInternal(vacancyDescription, cvContent);
        return extractRatingFromContent(response.getContent());
    }

    public InternalChatResponse generateRatingInternal(String vacancyDescription, String cvContent) {
        return generateInternalResponse(
            promptService.getRatingSystemPrompt(),
            promptService.getRatingUserPrompt(),
            vacancyDescription,
            cvContent
        );
    }
    
    private InternalChatResponse generateInternalResponse(String systemPrompt, String userTemplate, 
                                                        String vacancyDescription, String cvContent) {
        String userMessage = userTemplate
                .replace("{{vacancy_description}}", vacancyDescription)
                .replace("{{cv_content}}", cvContent);

        return getInternalChatResponse(systemPrompt, userMessage);
    }
    
    private int extractRatingFromContent(String content) {
        return Optional.ofNullable(content)
                .map(r -> r.replaceAll("[^0-9]", "").trim())
                .flatMap(this::parseIntSafe)
                .orElse(1);
    }

    private InternalChatResponse getInternalChatResponse(String system, String userMessage) {
        ChatResponse response = chatClient
                .build()
                .prompt()
                .system(system)
                .user(userMessage)
                .call()
                .chatResponse();

        // Extract content and token usage from the ChatResponse
        String content = response.getResult().getOutput().getText();
        int inputTokens = 0;
        int outputTokens = 0;

        if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            inputTokens = response.getMetadata().getUsage().getPromptTokens();
            outputTokens = response.getMetadata().getUsage().getCompletionTokens();
        }

        return new InternalChatResponse(content, inputTokens, outputTokens);
    }

  private Optional<Integer> parseIntSafe(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}