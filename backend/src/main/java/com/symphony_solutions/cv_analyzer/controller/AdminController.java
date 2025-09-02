package com.symphony_solutions.cv_analyzer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic admin controller for general admin operations.
 * Prompt management has been moved to PromptManagementController.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/status")
    public ResponseEntity<String> getAdminStatus() {
        return ResponseEntity.ok("Admin panel is accessible");
    }
}
