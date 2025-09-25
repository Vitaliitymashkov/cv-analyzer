package com.symphony_solutions.cv_analyzer.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
    return chatClientBuilder
        .defaultAdvisors(SimpleLoggerAdvisor.builder()
            .requestToString(request -> {
              String header = "\n************************** Request to LLM **************************\n";
              String footer = "\n************************** End of Request **************************";
              String userMessage = request.prompt().getUserMessage().getText();
              if (userMessage.length() > 500) {
                userMessage = userMessage.substring(0, 500) + "...[truncated]...";
              }
              return "%s[user message]:%s%s".formatted(header, userMessage, footer);
            })
            .responseToString(response -> {
              String header = "\n************************** Response from LLM **************************\n";
              String footer = "\n**************************  End of Response  **************************";
              String content = response.toString();
              return header + content + footer;
            })
            .build())
        .build();
  }

}
