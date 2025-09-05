package com.symphony_solutions.cv_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple error response DTO for API error handling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private int status;
  private String error;
  private String message;

  public static ErrorResponse of(int status, String error, String message) {
    return ErrorResponse.builder()
        .status(status)
        .error(error)
        .message(message)
        .build();
  }
}
