package com.symphony_solutions.cv_analyzer.controller;

import com.symphony_solutions.cv_analyzer.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PromptService promptService;

    @PostMapping("/prompts/refresh")
    public ResponseEntity<String> refreshPrompts() {
        promptService.refresh();
        return ResponseEntity.ok("Prompts reloaded");
    }
}
