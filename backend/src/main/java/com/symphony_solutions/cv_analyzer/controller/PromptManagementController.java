package com.symphony_solutions.cv_analyzer.controller;

import com.symphony_solutions.cv_analyzer.dto.PromptDto;
import com.symphony_solutions.cv_analyzer.dto.PromptUpdateRequest;
import com.symphony_solutions.cv_analyzer.exception.PromptManagementException;
import com.symphony_solutions.cv_analyzer.service.PromptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for prompt management operations.
 * Provides endpoints for viewing, updating, and managing AI prompts.
 * All endpoints require admin authentication.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/prompts")
@RequiredArgsConstructor
public class PromptManagementController {
    
    private final PromptService promptService;
    
    /**
     * Get all available prompts.
     * 
     * @return List of all prompts with their current content
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PromptDto>> getAllPrompts() {
        try {
            List<PromptDto> prompts = promptService.getAllPrompts();
            return ResponseEntity.ok(prompts);
        } catch (Exception e) {
            log.error("Failed to retrieve prompts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get a specific prompt by type and role.
     * 
     * @param type The prompt type (summary or rating)
     * @param role The prompt role (system or user)
     * @return The requested prompt
     */
    @GetMapping("/{type}/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromptDto> getPrompt(
            @PathVariable String type,
            @PathVariable String role) {
        try {
            PromptDto prompt = promptService.getPrompt(type, role);
            return ResponseEntity.ok(prompt);
        } catch (PromptManagementException e) {
            log.warn("Invalid prompt request: {}/{}", type, role, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to retrieve prompt: {}/{}", type, role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update a prompt with new content.
     * 
     * @param request The prompt update request
     * @return The updated prompt
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromptDto> updatePrompt(@RequestBody PromptUpdateRequest request) {
        try {
            PromptDto updatedPrompt = promptService.updatePrompt(request);
            log.info("Prompt updated successfully: {}/{}", request.getType(), request.getRole());
            return ResponseEntity.ok(updatedPrompt);
        } catch (PromptManagementException e) {
            log.warn("Invalid prompt update request", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to update prompt: {}/{}", request.getType(), request.getRole(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Reset a prompt to its default value.
     * 
     * @param type The prompt type (summary or rating)
     * @param role The prompt role (system or user)
     * @return The reset prompt
     */
    @PostMapping("/{type}/{role}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromptDto> resetPrompt(
            @PathVariable String type,
            @PathVariable String role) {
        try {
            PromptDto resetPrompt = promptService.resetPrompt(type, role);
            log.info("Prompt reset successfully: {}/{}", type, role);
            return ResponseEntity.ok(resetPrompt);
        } catch (PromptManagementException e) {
            log.warn("Invalid prompt reset request: {}/{}", type, role, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to reset prompt: {}/{}", type, role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Refresh all prompts from their files (reload cache).
     * 
     * @return Success message
     */
    @PostMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> refreshPrompts() {
        try {
            promptService.refresh();
            log.info("All prompts refreshed successfully");
            return ResponseEntity.ok("Prompts refreshed successfully");
        } catch (Exception e) {
            log.error("Failed to refresh prompts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to refresh prompts: " + e.getMessage());
        }
    }
}
