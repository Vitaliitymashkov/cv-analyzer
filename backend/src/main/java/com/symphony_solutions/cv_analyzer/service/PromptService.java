package com.symphony_solutions.cv_analyzer.service;

import com.symphony_solutions.cv_analyzer.config.RatingConfig;
import com.symphony_solutions.cv_analyzer.dto.PromptDto;
import com.symphony_solutions.cv_analyzer.dto.PromptUpdateRequest;
import com.symphony_solutions.cv_analyzer.dto.PromptType;
import com.symphony_solutions.cv_analyzer.exception.PromptManagementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptService {

    private final ResourceLoader resourceLoader;
    private final RatingConfig ratingConfig;

    @Value("${prompts.summary.system:classpath:prompts/summary/system.txt}")
    private String summarySystemPath;

    @Value("${prompts.summary.user:classpath:prompts/summary/user.txt}")
    private String summaryUserPath;

    @Value("${prompts.rating.system:classpath:prompts/rating/system.txt}")
    private String ratingSystemPath;

    @Value("${prompts.rating.user:classpath:prompts/rating/user.txt}")
    private String ratingUserPath;

    private volatile String cachedSummarySystem;
    private volatile String cachedSummaryUser;
    private volatile String cachedRatingSystem;
    private volatile String cachedRatingUser;

    @PostConstruct
    public void init() {
        reloadAll();
    }

    public synchronized void refresh() {
        reloadAll();
    }

    public String getSummarySystemPrompt() {
        return cachedSummarySystem;
    }

    public String getSummaryUserPrompt() {
        return cachedSummaryUser;
    }

    public String getRatingSystemPrompt() {
        return cachedRatingSystem.replace("{{rating_range}}", ratingConfig.getRangeDescription());
    }

    public String getRatingUserPrompt() {
        return cachedRatingUser
                .replace("{{rating_range}}", ratingConfig.getRangeDescription())
                .replace("{{max_rating}}", String.valueOf(ratingConfig.getMax()));
    }

    private void reloadAll() {
        cachedSummarySystem = readResource(summarySystemPath);
        cachedSummaryUser = readResource(summaryUserPath);
        cachedRatingSystem = readResource(ratingSystemPath);
        cachedRatingUser = readResource(ratingUserPath);
    }

    private String readResource(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            log.error("Failed to read resource: {}", location, e);
            // Fall back to empty string so callers can still send a prompt, though degraded
            return "";
        }
    }
    
    /**
     * Get all prompts as DTOs for management interface.
     */
    public List<PromptDto> getAllPrompts() {
        List<PromptDto> prompts = new ArrayList<>();
        prompts.add(new PromptDto(PromptType.SUMMARY, "system", cachedSummarySystem, summarySystemPath, true));
        prompts.add(new PromptDto(PromptType.SUMMARY, "user", cachedSummaryUser, summaryUserPath, true));
        prompts.add(new PromptDto(PromptType.RATING, "system", cachedRatingSystem, ratingSystemPath, true));
        prompts.add(new PromptDto(PromptType.RATING, "user", cachedRatingUser, ratingUserPath, true));
        return prompts;
    }
    
    /**
     * Get a specific prompt by type and role.
     */
    public PromptDto getPrompt(String typeStr, String role) {
        PromptType type;
        try {
            type = PromptType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            throw new PromptManagementException("Invalid prompt type: " + typeStr);
        }
        return switch (type) {
            case SUMMARY -> switch (role) {
                case "system" -> new PromptDto(PromptType.SUMMARY, "system", cachedSummarySystem, summarySystemPath, true);
                case "user" -> new PromptDto(PromptType.SUMMARY, "user", cachedSummaryUser, summaryUserPath, true);
                default -> throw new PromptManagementException("Invalid role: " + role);
            };
            case RATING -> switch (role) {
                case "system" -> new PromptDto(PromptType.RATING, "system", cachedRatingSystem, ratingSystemPath, true);
                case "user" -> new PromptDto(PromptType.RATING, "user", cachedRatingUser, ratingUserPath, true);
                default -> throw new PromptManagementException("Invalid role: " + role);
            };
        };
    }
    
    /**
     * Update a prompt and save it to file.
     */
    public synchronized PromptDto updatePrompt(PromptUpdateRequest request) {
        validatePromptUpdateRequest(request);
        PromptType type = request.getType();
        String filePath = getFilePath(type, request.getRole());
        String content = request.getContent();
        try {
            writeToFile(filePath, content);
            updateCache(type, request.getRole(), content);
            log.info("Successfully updated prompt: {}/{}", type, request.getRole());
            return new PromptDto(type, request.getRole(), content, filePath, true);
        } catch (Exception e) {
            log.error("Failed to update prompt: {}/{}", type, request.getRole(), e);
            throw new PromptManagementException("Failed to update prompt: " + e.getMessage(), e);
        }
    }
    
    /**
     * Reset a prompt to its default value from the classpath resource.
     */
    public synchronized PromptDto resetPrompt(String typeStr, String role) {
        PromptType type;
        try {
            type = PromptType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            throw new PromptManagementException("Invalid prompt type: " + typeStr);
        }
        String filePath = getFilePath(type, role);
        String originalContent = readResource(filePath);
        try {
            writeToFile(filePath, originalContent);
            updateCache(type, role, originalContent);
            log.info("Successfully reset prompt: {}/{}", type, role);
            return new PromptDto(type, role, originalContent, filePath, true);
        } catch (Exception e) {
            log.error("Failed to reset prompt: {}/{}", type, role, e);
            throw new PromptManagementException("Failed to reset prompt: " + e.getMessage(), e);
        }
    }
    
    private void validatePromptUpdateRequest(PromptUpdateRequest request) {
        if (request == null) {
            throw new PromptManagementException("Prompt update request cannot be null");
        }
        if (request.getType() == null) {
            throw new PromptManagementException("Prompt type cannot be null");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new PromptManagementException("Prompt role cannot be null or empty");
        }
        if (request.getContent() == null) {
            throw new PromptManagementException("Prompt content cannot be null");
        }
        if (!isValidPromptRole(request.getRole())) {
            throw new PromptManagementException("Invalid prompt role: " + request.getRole());
        }
    }
    
    private boolean isValidPromptRole(String role) {
        return "system".equals(role) || "user".equals(role);
    }
    
    private String getFilePath(PromptType type, String role) {
        return switch (type) {
            case SUMMARY -> switch (role) {
                case "system" -> summarySystemPath;
                case "user" -> summaryUserPath;
                default -> throw new PromptManagementException("Invalid prompt role: " + role);
            };
            case RATING -> switch (role) {
                case "system" -> ratingSystemPath;
                case "user" -> ratingUserPath;
                default -> throw new PromptManagementException("Invalid prompt role: " + role);
            };
        };
    }
    
    private void updateCache(PromptType type, String role, String content) {
        switch (type) {
            case SUMMARY -> {
                if ("system".equals(role)) cachedSummarySystem = content;
                else if ("user".equals(role)) cachedSummaryUser = content;
            }
            case RATING -> {
                if ("system".equals(role)) cachedRatingSystem = content;
                else if ("user".equals(role)) cachedRatingUser = content;
            }
        }
    }
    
    private void writeToFile(String filePath, String content) throws Exception {
        // Handle classpath resources by converting to file path
        String actualFilePath = filePath;
        if (filePath.startsWith("classpath:")) {
            actualFilePath = filePath.substring("classpath:".length());
        }
        
        // Get the actual file path in the resources directory
        Path resourcePath = Paths.get("src/main/resources", actualFilePath);
        
        // Ensure parent directories exist
        Files.createDirectories(resourcePath.getParent());
        
        // Write content to file
        try (BufferedWriter writer = Files.newBufferedWriter(resourcePath, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        
        log.debug("Written prompt content to file: {}", resourcePath);
    }
}
