package com.symphony_solutions.cv_analyzer.service;

import com.symphony_solutions.cv_analyzer.model.Resume;
import java.util.List;

public interface ResumeService {

  List<Resume> findTopCandidates(String vacancyDescription, int limit);
}
