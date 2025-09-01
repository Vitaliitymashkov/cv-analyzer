package com.symphony_solutions.cv_analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentService {

  private final ChatClient.Builder chatClient;

  public String generateResponse(String prompt) {
    return chatClient.build().prompt(prompt).call().content();
  }
}
