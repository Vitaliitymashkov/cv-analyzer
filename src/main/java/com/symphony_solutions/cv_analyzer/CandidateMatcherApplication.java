package com.symphony_solutions.cv_analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class CandidateMatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(CandidateMatcherApplication.class, args);
	}

	@Bean
	ApplicationRunner apiKeyLogger(Environment env) {
		return args -> {
			String key = env.getProperty("spring.ai.openai.api-key");
			if (key == null || key.isBlank()) {
				log.info("[startup] OPENAI_API_KEY not resolved (spring.ai.openai.api-key is empty)");
			} else {
				String masked = key.length() <= 6 ? "******" : key.substring(0, 3) + "***" + key.substring(key.length() - 3);
				log.info("[startup] OPENAI_API_KEY resolved via configuration: {}", masked);
			}
		};
	}
}
