package com.symphony_solutions.cv_analyzer.service;

import com.symphony_solutions.cv_analyzer.exception.ResumeParsingException;
import com.symphony_solutions.cv_analyzer.model.Resume;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResumeFileService implements ResumeService {

  private static final String TXT_PATTERN = "classpath:cvs/*.txt";

  private static final String PDF_PATTERN = "classpath:cvs/*.pdf";

  /**
   * Returns the top N candidates most relevant to the vacancy description.
   */
  @Override
  public List<Resume> findTopCandidates(String vacancyDescription, int limit) {
    String[] keywords = extractKeywords(vacancyDescription);
    List<Resume> allCvs = loadAllCvs();

    return allCvs.stream()
        .sorted(byRelevanceScore(keywords))
        .limit(limit)
        .toList();
  }

  /**
   * Loads all CVs from resources (txt and pdf).
   */
  private List<Resume> loadAllCvs() {
    List<Resume> cvs = new ArrayList<>();
    cvs.addAll(loadTextCvs());
    cvs.addAll(loadPdfCvs());
    return cvs;
  }

  private String[] extractKeywords(String vacancyDescription) {
    return vacancyDescription.toLowerCase().split("\\W+");
  }

  private Comparator<Resume> byRelevanceScore(String[] keywords) {
    return Comparator.comparingInt((Resume cv) -> matchScore(cv, keywords)).reversed();
  }

  private List<Resume> loadTextCvs() {
    return loadResources(TXT_PATTERN).stream()
        .map(this::parseTextCv)
        .toList();
  }

  private List<Resume> loadPdfCvs() {
    return loadResources(PDF_PATTERN).stream()
        .map(this::parsePdfCv)
        .toList();
  }

  private List<Resource> loadResources(String pattern) {
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      return Arrays.asList(resolver.getResources(pattern));
    } catch (Exception e) {
      log.error("Failed to load resources for pattern: {}", pattern, e);
      return Collections.emptyList();
    }
  }

  private Resume parseTextCv(Resource resource) {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      String filename = Optional.ofNullable(resource.getFilename()).orElse("Unknown");
      String name = extractNameFromFilename(filename, ".txt");
      String content = reader.lines().collect(Collectors.joining("\n"));
      return Resume.builder()
          .name(name)
          .content(content)
          .filename(filename)
          .build();
    } catch (Exception e) {
      log.error("Failed to parse text CV: {}", resource.getFilename(), e);
      throw new ResumeParsingException("Failed to parse text CV: " + resource.getFilename(), e);
    }
  }

  private Resume parsePdfCv(Resource resource) {
    try (PDDocument document = PDDocument.load(resource.getInputStream())) {
      String filename = Optional.ofNullable(resource.getFilename()).orElse("Unknown");
      String name = extractNameFromFilename(filename, ".pdf");
      PDFTextStripper pdfStripper = new PDFTextStripper();
      String content = pdfStripper.getText(document);
      return Resume.builder()
          .name(name)
          .content(content)
          .filename(filename)
          .build();
    } catch (Exception e) {
      log.error("Failed to parse PDF CV: {}", resource.getFilename(), e);
      throw new ResumeParsingException("Failed to parse PDF CV: " + resource.getFilename(), e);
    }
  }

  private String extractNameFromFilename(String filename, String extension) {
    return filename.replace(extension, "");
  }

  private int matchScore(Resume cv, String[] keywords) {
    String content = cv.getContent().toLowerCase();
    return (int) Arrays.stream(keywords)
        .filter(content::contains)
        .count();
  }
}