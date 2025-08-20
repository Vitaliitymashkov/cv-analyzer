package com.symphony_solutions.cv_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

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
				System.out.println("[startup] OPENAI_API_KEY not resolved (spring.ai.openai.api-key is empty)");
			} else {
				String masked = key.length() <= 6 ? "******" : key.substring(0, 3) + "***" + key.substring(key.length() - 3);
				System.out.println("[startup] OPENAI_API_KEY resolved via configuration: " + masked);
			}
		};
	}
}
