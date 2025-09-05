package com.symphony_solutions.cv_analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSummaryResponseDto {

  private String name;

  private String filename;

  private String summary;

  private int rating;
}
