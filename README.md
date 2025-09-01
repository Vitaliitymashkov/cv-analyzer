# CV Analyzer

A full-stack application that matches candidate CVs (PDF or TXT) to job vacancy descriptions using LLM-powered summaries and ratings.

## Application Overview

- **Backend**: Spring Boot REST API with OpenAI integration
- **Frontend**: React web application with modern UI
- **Features**: CV analysis, candidate matching, and rating system

## Features

### Backend Features
- Upload CVs as `.pdf` or `.txt` files to `backend/src/main/resources/cvs/`
- REST API to find the best-matching candidates for a job description
- Uses OpenAI (or compatible) LLM to generate fit summaries and ratings
- Externalized prompt templates with System/User roles for robust, tunable inference
- **GenAI metrics exposed via Spring Boot Actuator endpoints for LLM usage monitoring**

### Frontend Features
- Modern React web interface
- Upload and manage CV files
- Submit job descriptions for candidate matching
- View candidate summaries and ratings
- Responsive design for desktop and mobile

## Prerequisites

### Option 1: Local Development
- Java 17+
- Maven 3.8+
- Node.js 18+ (for frontend)
- An OpenAI API key (or compatible endpoint)

### Option 2: Docker (Recommended)
- Docker
- Docker Compose
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

3. **Add candidate CVs**

Place `.pdf` and/or `.txt` files in:

```
backend/src/main/resources/cvs/
```

Each file should represent one candidate's resume.

## Running the Application

### Option 1: Using Docker (Recommended)

The easiest way to run the application is using Docker Compose:

```bash
# Start all services
docker-compose up -d

# Or use the management script
./docker-scripts.sh start
```

The application will be available at:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080

**Other useful Docker commands:**
```bash
# Check service status
./docker-scripts.sh status

# View logs
./docker-scripts.sh logs

# Stop services
./docker-scripts.sh stop

# Development mode with hot reloading
./docker-scripts.sh dev
```

### Option 2: Local Development

If you prefer to run the services locally:

**Backend:**
```bash
cd backend
./mvnw clean package
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm start
```

The backend will start on [http://localhost:8080](http://localhost:8080) and the frontend on [http://localhost:3000](http://localhost:3000).

## Project Structure

```
cv-analyzer/
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration and resources
│   │   ├── cvs/           # CV files directory
│   │   └── prompts/       # AI prompt templates
│   └── Dockerfile         # Backend Docker configuration
├── frontend/               # React application
│   ├── src/               # React source code
│   ├── public/            # Static assets
│   └── Dockerfile         # Frontend Docker configuration
├── docker-compose.yml     # Production Docker setup
├── docker-compose.dev.yml # Development Docker setup
├── docker-scripts.sh      # Docker management script
└── .env                   # Environment variables
```

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
