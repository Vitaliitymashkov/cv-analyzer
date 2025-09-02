package com.symphony_solutions.cv_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cv {

    private String name;

    private String content;

    private String filename;
} 