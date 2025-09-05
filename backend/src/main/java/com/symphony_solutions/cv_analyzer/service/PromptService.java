package com.symphony_solutions.cv_analyzer.service;

import com.symphony_solutions.cv_analyzer.config.RatingConfig;
import com.symphony_solutions.cv_analyzer.dto.PromptDto;
import com.symphony_solutions.cv_analyzer.dto.PromptUpdateRequest;
import com.symphony_solutions.cv_analyzer.dto.type.PromptRole;
import com.symphony_solutions.cv_analyzer.dto.type.PromptType;
import com.symphony_solutions.cv_analyzer.exception.PromptManagementException;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

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
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
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
    prompts.add(new PromptDto(PromptType.SUMMARY, PromptRole.SYSTEM, cachedSummarySystem, summarySystemPath, true));
    prompts.add(new PromptDto(PromptType.SUMMARY, PromptRole.USER, cachedSummaryUser, summaryUserPath, true));
    prompts.add(new PromptDto(PromptType.RATING, PromptRole.SYSTEM, cachedRatingSystem, ratingSystemPath, true));
    prompts.add(new PromptDto(PromptType.RATING, PromptRole.USER, cachedRatingUser, ratingUserPath, true));
    return prompts;
  }

  /**
   * Get a specific prompt by type and role.
   */
  public PromptDto getPrompt(String typeStr, String roleStr) {
    PromptType type;
    PromptRole role;
    try {
      type = PromptType.valueOf(typeStr.toUpperCase());
      role = PromptRole.valueOf(roleStr.toUpperCase());
    } catch (Exception e) {
      throw new PromptManagementException("Invalid prompt type or role: " + typeStr + "/" + roleStr);
    }
    return switch (type) {
      case SUMMARY -> switch (role) {
        case SYSTEM ->
            new PromptDto(PromptType.SUMMARY, PromptRole.SYSTEM, cachedSummarySystem, summarySystemPath, true);
        case USER -> new PromptDto(PromptType.SUMMARY, PromptRole.USER, cachedSummaryUser, summaryUserPath, true);
      };
      case RATING -> switch (role) {
        case SYSTEM -> new PromptDto(PromptType.RATING, PromptRole.SYSTEM, cachedRatingSystem, ratingSystemPath, true);
        case USER -> new PromptDto(PromptType.RATING, PromptRole.USER, cachedRatingUser, ratingUserPath, true);
      };
    };
  }

  /**
   * Update a prompt and save it to file.
   */
  public synchronized PromptDto updatePrompt(PromptUpdateRequest request) {
    validatePromptUpdateRequest(request);
    PromptType type = request.getType();
    PromptRole role = request.getRole();
    String filePath = getFilePath(type, role);
    String content = request.getContent();
    try {
      writeToFile(filePath, content);
      updateCache(type, role, content);
      log.info("Successfully updated prompt: {}/{}", type, role);
      return new PromptDto(type, role, content, filePath, true);
    } catch (Exception e) {
      log.error("Failed to update prompt: {}/{}", type, role, e);
      throw new PromptManagementException("Failed to update prompt: " + e.getMessage(), e);
    }
  }

  /**
   * Reset a prompt to its default value from the classpath resource.
   */
  public synchronized PromptDto resetPrompt(String typeStr, String roleStr) {
    PromptType type;
    PromptRole role;
    try {
      type = PromptType.valueOf(typeStr.toUpperCase());
      role = PromptRole.valueOf(roleStr.toUpperCase());
    } catch (Exception e) {
      throw new PromptManagementException("Invalid prompt type or role: " + typeStr + "/" + roleStr);
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
    if (request.getRole() == null) {
      throw new PromptManagementException("Prompt role cannot be null");
    }
    if (request.getContent() == null) {
      throw new PromptManagementException("Prompt content cannot be null");
    }
  }

  private String getFilePath(PromptType type, PromptRole role) {
    return switch (type) {
      case SUMMARY -> switch (role) {
        case SYSTEM -> summarySystemPath;
        case USER -> summaryUserPath;
      };
      case RATING -> switch (role) {
        case SYSTEM -> ratingSystemPath;
        case USER -> ratingUserPath;
      };
    };
  }

  private void updateCache(PromptType type, PromptRole role, String content) {
    if (Objects.requireNonNull(type) == PromptType.SUMMARY) {
      if (role == PromptRole.SYSTEM) {
        cachedSummarySystem = content;
      } else if (role == PromptRole.USER) {
        cachedSummaryUser = content;
      }
    } else if (type == PromptType.RATING) {
      if (role == PromptRole.SYSTEM) {
        cachedRatingSystem = content;
      } else if (role == PromptRole.USER) {
        cachedRatingUser = content;
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
