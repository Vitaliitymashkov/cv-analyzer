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
- **Real-time cost tracking with actual token usage via Aspect-Oriented Programming (AOP)**
- **Configurable OpenAI pricing with automatic cost calculation**
- **Health monitoring and system metrics via Spring Boot Actuator**

### Frontend Features
- **Modern React web interface with Chakra UI components**
- **Responsive sidebar navigation with collapsible menu**
- **Dark/Light theme toggle with persistent user preference**
- Upload and manage CV files
- Submit job descriptions for candidate matching
- View candidate summaries and ratings
- **Real-time Health & Metrics dashboard with auto-refresh**
- **GenAI cost tracking and token usage visualization**
- **System health monitoring with detailed component status**
- Responsive design for desktop, tablet, and mobile

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

**Optional: Configure OpenAI pricing (defaults to GPT-4o pricing)**
```
OPENAI_PRICING_INPUT=0.1
OPENAI_PRICING_OUTPUT=0.4
OPENAI_PRICING_CURRENCY=USD
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
- **Health & Metrics Dashboard**: http://localhost:3000/health
- **Admin Panel**: http://localhost:3000/admin

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
│   │   ├── aspect/         # AOP aspects for cross-cutting concerns
│   │   ├── config/         # Spring configuration classes
│   │   ├── controller/     # REST controllers
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── exception/     # Custom exception classes
│   │   ├── model/         # Domain models and internal data structures
│   │   └── service/       # Business logic services
│   ├── src/main/resources/ # Configuration and resources
│   │   ├── cvs/           # CV files directory
│   │   ├── prompts/       # AI prompt templates
│   │   └── application.properties # Application configuration
│   └── Dockerfile         # Backend Docker configuration
├── frontend/               # React application
│   ├── src/               # React source code
│   │   ├── components/    # Reusable UI components
│   │   ├── pages/         # Page components
│   │   └── App.js         # Main application component
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

### Cost Metrics Endpoint

**GET** `/api/cost/metrics`

**Response:**
```json
{
  "totalCost": 0.0045,
  "totalInputTokens": 1360,
  "totalOutputTokens": 114,
  "pricing": {
    "inputTokensPerMillion": 0.1,
    "outputTokensPerMillion": 0.4,
    "currency": "USD"
  }
}
```

### Pricing Information Endpoint

**GET** `/api/cost/pricing`

**Response:**
```json
{
  "inputTokensPerMillion": 0.1,
  "outputTokensPerMillion": 0.4,
  "currency": "USD"
}
```

### GenAI Metrics via Actuator

Spring Boot Actuator exposes health, info, and metrics endpoints.  
GenAI metrics are available at:

- `/actuator/health` - System health status
- `/actuator/metrics/gen_ai.client.operation` - Total LLM operations
- `/actuator/metrics/gen_ai.client.operation.active` - Active operations
- `/actuator/metrics/gen_ai.client.token.usage` - Token usage statistics
- `/api/cost/metrics` - **Real-time cost tracking with actual token usage**
- `/api/cost/pricing` - **Current pricing configuration**

These endpoints provide insights into LLM usage, performance, and costs.

### Health & Metrics Dashboard

The application includes a comprehensive **Health & Metrics Dashboard** accessible at `/health` that provides:

- **System Health Status**: Real-time backend health monitoring
- **GenAI Operations**: Total LLM API calls made
- **Token Usage**: Input/output token consumption with detailed breakdown
- **Cost Tracking**: Real-time cost calculation based on actual token usage
- **Pricing Information**: Current OpenAI pricing configuration
- **Auto-refresh**: Updates every 30 seconds automatically
- **Responsive Design**: Works on desktop, tablet, and mobile

### Cost Tracking Features

The application now includes **comprehensive cost tracking**:

- **Real-time Token Usage**: Tracks actual input/output tokens from OpenAI API responses
- **Automatic Cost Calculation**: Uses configurable pricing to calculate costs
- **AOP-based Tracking**: Uses Aspect-Oriented Programming for clean separation of concerns
- **Micrometer Integration**: Exposes cost metrics via Spring Boot Actuator
- **Configurable Pricing**: Set custom pricing via environment variables
- **Historical Tracking**: Maintains cumulative cost and token usage

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

## User Interface Features

### Navigation & Theming
- **Collapsible Sidebar**: Responsive navigation menu that adapts to screen size
- **Dark/Light Theme**: Toggle between themes with persistent user preference
- **Mobile-First Design**: Optimized for mobile, tablet, and desktop viewing
- **Chakra UI Components**: Modern, accessible UI components throughout

### Pages & Functionality
- **Candidate Search** (`/`): Main page for job description input and candidate matching
- **Admin Panel** (`/admin`): Prompt refresh and administrative functions
- **Health & Metrics** (`/health`): Real-time system monitoring and cost tracking

## Technical Architecture

### Backend Architecture
- **Spring Boot**: REST API with embedded Tomcat server
- **Aspect-Oriented Programming (AOP)**: Clean separation of cross-cutting concerns like cost tracking
- **Micrometer**: Metrics collection and exposure via Spring Boot Actuator
- **OpenAI Integration**: Direct API integration with token usage tracking
- **Exception Handling**: Custom exceptions with proper error propagation
- **Clean Code Principles**: Following Uncle Bob's Clean Code guidelines with small functions, meaningful names, and DRY principles

### Frontend Architecture
- **React 18**: Modern React with hooks and functional components
- **Chakra UI**: Component library with built-in theming and accessibility
- **React Router**: Client-side routing with nested routes
- **Axios**: HTTP client for API communication
- **Responsive Design**: Mobile-first approach with breakpoint-based layouts

## Notes

- The OpenAI API key is required for LLM-powered summaries and ratings.
- The app supports both `.pdf` and `.txt` CVs.
- Summaries and ratings are generated per candidate using the LLM.
- **Cost tracking is automatic** and based on actual token usage from OpenAI API responses.
- **All metrics are exposed** via Spring Boot Actuator for monitoring and alerting.
- **Pricing is configurable** via environment variables for different OpenAI models.

## License

MIT
