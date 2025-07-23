# Candidate Matcher

A Spring Boot application that matches candidate CVs (PDF or TXT) to a job vacancy description using LLM-powered summaries and ratings.

## Features
- Upload CVs as `.pdf` or `.txt` files to `src/main/resources/cvs/`
- REST API to find the best-matching candidates for a job description
- Uses OpenAI (or compatible) LLM to generate fit summaries and ratings

## Prerequisites
- Java 17+
- Maven 3.8+
- An OpenAI API key (or compatible endpoint)

## Setup

1. **Clone the repository**

```bash
git clone <your-repo-url>
cd candidate-matcher
```

2. **Add your OpenAI API key**

Create a `.env` file in the project root:

```
OPEN_AI_API_KEY=sk-...your-key...
```

Alternatively, set the environment variable in your shell:

```bash
export OPEN_AI_API_KEY=sk-...your-key...
```

3. **Add candidate CVs**

Place `.pdf` and/or `.txt` files in:
```
src/main/resources/cvs/
```
Each file should represent one candidate's resume.

4. **Build and run the application**

```bash
mvn clean package
mvn spring-boot:run
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

## Notes
- The OpenAI API key is required for LLM-powered summaries and ratings.
- The app supports both `.pdf` and `.txt` CVs.
- Summaries and ratings are generated per candidate using the LLM.

## License
MIT 