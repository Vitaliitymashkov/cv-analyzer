# Candidate Matcher

A Spring Boot application that matches candidate CVs (PDF or TXT) to a job vacancy description using LLM-powered summaries and ratings.

## Features

- Upload CVs as `.pdf` or `.txt` files to `src/main/resources/cvs/`
- REST API to find the best-matching candidates for a job description
- Uses OpenAI (or compatible) LLM to generate fit summaries and ratings
- Externalized prompt templates with System/User roles for robust, tunable inference
- **GenAI metrics exposed via Spring Boot Actuator endpoints for LLM usage monitoring**

## Prerequisites

- Java 17+
- Maven 3.8+
- An OpenAI API key (or compatible endpoint)

## Setup

1. **Clone the repository**

```bash
git clone <your-repo-url>
cd cv-analyzer
```

2. **Add your OpenAI API key**

Create a `.env` file in the project root:

```
OPENAI_API_KEY=sk-...your-key...
```

Alternatively, set the environment variable in your shell:

```bash
export OPENAI_API_KEY=sk-...your-key...
```

3. **Add candidate CVs**

Place `.pdf` and/or `.txt` files in:

```
src/main/resources/cvs/
```

Each file should represent one candidate's resume.

4. **Build and run the application**

```bash
./mvnw clean package
./mvnw spring-boot:run
```

The app will start on [http://localhost:8080](http://localhost:8080).

## API Usage

### Match Candidates Endpoint

**POST** `/api/candidate-matcher/match`

**Request Body:**

```json
{
  "vacancyDescription": "Looking for a Java developer with Spring Boot and PDF processing experience."
}
```

**Response:**

```json
[
  {
    "name": "John Doe",
    "filename": "john_doe.pdf",
    "summary": "John has extensive experience in Java and Spring Boot, as well as hands-on PDF processing. This makes him an excellent fit for the role described.",
    "rating": 11
  },
  ...
]
```

### Example cURL

```bash
curl -X POST http://localhost:8080/api/candidate-matcher/match \
  -H "Content-Type: application/json" \
  -d '{"vacancyDescription": "Looking for a Java developer with Spring Boot and PDF processing experience."}'
```

### GenAI Metrics via Actuator

Spring Boot Actuator exposes health, info, and metrics endpoints.  
GenAI metrics are available at:

- `/actuator/metrics/gen_ai.client.operation`
- `/actuator/metrics/gen_ai.client.operation.active`
- `/actuator/metrics/gen_ai.client.token.usage`

These endpoints provide insights into LLM usage and performance.

## Prompt customization (System/User roles)

- Prompts are externalized to files under `src/main/resources/prompts`:
  - Summary system: `prompts/summary/system.txt`
  - Summary user: `prompts/summary/user.txt`
  - Rating system: `prompts/rating/system.txt`
  - Rating user: `prompts/rating/user.txt`
- Placeholders available in user templates:
  - `{{vacancy_description}}`
  - `{{cv_content}}`
- You can override prompt file locations without changing code:
  - In `application.properties`:
    - `prompts.summary.system=classpath:prompts/summary/system.txt`
    - `prompts.summary.user=classpath:prompts/summary/user.txt`
    - `prompts.rating.system=classpath:prompts/rating/system.txt`
    - `prompts.rating.user=classpath:prompts/rating/user.txt`
  - Or via environment variables (Spring property syntax):
    - `PROMPTS_SUMMARY_SYSTEM=file:/abs/path/summary-system.txt`
    - `PROMPTS_SUMMARY_USER=file:/abs/path/summary-user.txt`
    - `PROMPTS_RATING_SYSTEM=file:/abs/path/rating-system.txt`
    - `PROMPTS_RATING_USER=file:/abs/path/rating-user.txt`
- The app sends role-based messages to the LLM: a System instruction plus a User message rendered from the templates with your placeholders.

### Runtime prompt refresh (no restart)

- Prompts are cached at startup for performance. After editing prompt files, refresh them at runtime via the admin endpoint:

```bash
curl -u admin:admin -X POST http://localhost:8080/api/admin/prompts/refresh
```

- Response: `200 OK` with body `Prompts reloaded`.
- This reloads all configured prompt files (system/user, summary/rating) without restarting the app.

### Securing the admin endpoint

- The `/api/admin/**` endpoints are protected with HTTP Basic auth.
- Default credentials (override in production):
  - Username: `admin` (property `admin.username` or env `ADMIN_USERNAME`)
  - Password: `admin` (property `admin.password` or env `ADMIN_PASSWORD`)
- Example with custom credentials:

```bash
export ADMIN_USERNAME=myadmin
export ADMIN_PASSWORD=strongsecret
# then restart the app and use
curl -u "$ADMIN_USERNAME:$ADMIN_PASSWORD" -X POST http://localhost:8080/api/admin/prompts/refresh
```

## Notes

- The OpenAI API key is required for LLM-powered summaries and ratings.
- The app supports both `.pdf` and `.txt` CVs.
- Summaries and ratings are generated per candidate using the LLM.

## License

MIT
