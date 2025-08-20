package com.symphony_solutions.cv_analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final ResourceLoader resourceLoader;

    @Value("${prompts.summary.system:classpath:prompts/summary/system.txt}")
    private String summarySystemPath;

    @Value("${prompts.summary.user:classpath:prompts/summary/user.txt}")
    private String summaryUserPath;

    @Value("${prompts.rating.system:classpath:prompts/rating/system.txt}")
    private String ratingSystemPath;

    @Value("${prompts.rating.user:classpath:prompts/rating/user.txt}")
    private String ratingUserPath;

    public String getSummarySystemPrompt() {
        return readResource(summarySystemPath);
    }

    public String getSummaryUserPrompt() {
        return readResource(summaryUserPath);
    }

    public String getRatingSystemPrompt() {
        return readResource(ratingSystemPath);
    }

    public String getRatingUserPrompt() {
        return readResource(ratingUserPath);
    }

    private String readResource(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            // Fall back to empty string so callers can still send a prompt, though degraded
            return "";
        }
    }
} 