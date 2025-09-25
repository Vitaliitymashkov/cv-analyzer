package com.symphony_solutions.cv_analyzer.service;

import com.symphony_solutions.cv_analyzer.config.RatingConfig;
import com.symphony_solutions.cv_analyzer.model.InternalChatResponse;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentSummaryService {

  private final ChatClient chatClient;
  private final PromptService promptService;
  private final RatingConfig ratingConfig;

  public InternalChatResponse generateSummary(String vacancyDescription, String cvContent) {
    return generateInternalResponse(
        promptService.getSummarySystemPrompt(),
        promptService.getSummaryUserPrompt(),
        vacancyDescription,
        cvContent
    );
  }

  public InternalChatResponse generateRating(String vacancyDescription, String cvContent) {
    return generateInternalResponse(
        promptService.getRatingSystemPrompt(),
        promptService.getRatingUserPrompt(),
        vacancyDescription,
        cvContent
    );
  }

  private InternalChatResponse generateInternalResponse(String systemText, String userText,
                                                        String vacancyDescription, String cvContent) {

    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
    PromptTemplate userPromptTemplate = PromptTemplate.builder()
        .template(userText)
        .build();
    var userMessage = userPromptTemplate.createMessage(
        Map.of(
            "vacancy_description", vacancyDescription, "cv_content", cvContent
        )
    );
    Prompt prompt = Prompt.builder()
        .messages(systemPromptTemplate.createMessage(), userMessage)
        .build();
    return getInternalChatResponse(prompt);
  }

  public int extractRatingFromContent(String content) {
    return Optional.ofNullable(content)
        .map(rating -> rating.replaceAll("\\D", "").trim())
        .flatMap(this::parseIntSafe)
        .map(rating -> Math.max(ratingConfig.getMin(), Math.min(rating, ratingConfig.getMax())))
        .orElse(ratingConfig.getMin());
  }

  private InternalChatResponse getInternalChatResponse(Prompt prompt) {
    try {
      ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

      // Extract content and token usage from the ChatResponse
      String content = response.getResult().getOutput().getText();
      int inputTokens = 0;
      int outputTokens = 0;

      if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
        inputTokens = response.getMetadata().getUsage().getPromptTokens();
        outputTokens = response.getMetadata().getUsage().getCompletionTokens();
      }

      return InternalChatResponse.builder()
          .content(content)
          .inputTokens(inputTokens)
          .outputTokens(outputTokens)
          .build();
    } catch (NonTransientAiException e) {
      log.error("AI service error in getInternalChatResponse: {}", e.getMessage(), e);
      // Re-throw the exception so it can be handled by the controller
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error in getInternalChatResponse: {}", e.getMessage(), e);
      // Wrap other exceptions in NonTransientAiException for consistent handling
      throw new NonTransientAiException("AI service error: " + e.getMessage(), e);
    }
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