package com.symphony_solutions.cv_analyzer.model;

import java.util.List;

public record UserPrompt(String text, String vacancyDescription, List<String> candidates) {
}

