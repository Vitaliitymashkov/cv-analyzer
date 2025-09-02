package com.symphony_solutions.cv_analyzer.service;

import com.symphony_solutions.cv_analyzer.exception.CvParsingException;
import com.symphony_solutions.cv_analyzer.model.Cv;
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
public class CvService {

    private static final String TXT_PATTERN = "classpath:cvs/*.txt";

    private static final String PDF_PATTERN = "classpath:cvs/*.pdf";

    /**
     * Loads all CVs from resources (txt and pdf).
     */
    public List<Cv> loadAllCvs() {
        List<Cv> cvs = new ArrayList<>();
        cvs.addAll(loadTextCvs());
        cvs.addAll(loadPdfCvs());
        return cvs;
    }

    /**
     * Returns the top N candidates most relevant to the vacancy description.
     */
    public List<Cv> findTopCandidates(String vacancyDescription, int limit) {
        String[] keywords = extractKeywords(vacancyDescription);
        List<Cv> allCvs = loadAllCvs();
        
        return allCvs.stream()
                .sorted(byRelevanceScore(keywords))
                .limit(limit)
                .toList();
    }
    
    private String[] extractKeywords(String vacancyDescription) {
        return vacancyDescription.toLowerCase().split("\\W+");
    }
    
    private Comparator<Cv> byRelevanceScore(String[] keywords) {
        return Comparator.comparingInt((Cv cv) -> matchScore(cv, keywords)).reversed();
    }

    private List<Cv> loadTextCvs() {
        return loadResources(TXT_PATTERN).stream()
                .map(this::parseTextCv)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Cv> loadPdfCvs() {
        return loadResources(PDF_PATTERN).stream()
                .map(this::parsePdfCv)
                .filter(Objects::nonNull)
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

    private Cv parseTextCv(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String filename = Optional.ofNullable(resource.getFilename()).orElse("Unknown");
            String name = extractNameFromFilename(filename, ".txt");
            String content = reader.lines().collect(Collectors.joining("\n"));
            return new Cv(name, content, filename);
        } catch (Exception e) {
            log.error("Failed to parse text CV: {}", resource.getFilename(), e);
            throw new CvParsingException("Failed to parse text CV: " + resource.getFilename(), e);
        }
    }

    private Cv parsePdfCv(Resource resource) {
        try (PDDocument document = PDDocument.load(resource.getInputStream())) {
            String filename = Optional.ofNullable(resource.getFilename()).orElse("Unknown");
            String name = extractNameFromFilename(filename, ".pdf");
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String content = pdfStripper.getText(document);
            return new Cv(name, content, filename);
        } catch (Exception e) {
            log.error("Failed to parse PDF CV: {}", resource.getFilename(), e);
            throw new CvParsingException("Failed to parse PDF CV: " + resource.getFilename(), e);
        }
    }
    
    private String extractNameFromFilename(String filename, String extension) {
        return filename.replace(extension, "");
    }

    private int matchScore(Cv cv, String[] keywords) {
        String content = cv.getContent().toLowerCase();
        return (int) Arrays.stream(keywords)
                .filter(content::contains)
                .count();
    }
}