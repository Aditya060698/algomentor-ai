# AlgoMentor AI

AlgoMentor AI is a production-oriented full-stack practice platform for Data Structures & Algorithms and System Design preparation. It combines rule-based evaluation, RAG-style concept retrieval, and an agentic orchestration workflow to generate grounded explanations, evaluate learner answers, and surface structured feedback.

This project is designed to be resume-ready and interview-friendly. It demonstrates backend architecture, frontend product flow, heuristic evaluation systems, DSA-driven internal implementations, and deployment thinking for a real-world AI application.

## Why This Project Is Strong

AlgoMentor AI is not just a CRUD app with an AI API call added on top. It includes:
- a layered Spring Boot backend with controllers, services, repositories, DTOs, and entities
- a React + TypeScript frontend with routed product flows
- a lightweight RAG pipeline for concept retrieval
- an agentic multi-step workflow for classification, retrieval, and structured generation
- a DSA evaluation engine using `HashSet`, `Trie`, `PriorityQueue`, and `HashMap`
- a heuristic System Design evaluator focused on components, bottlenecks, scalability, and trade-offs
- deployment scaffolding for Render using Docker and environment-based configuration

## System Architecture

### High-Level Flow

1. The user opens the frontend and browses DSA/System Design questions.
2. The frontend fetches questions from the Spring Boot backend.
3. The user practices and submits an answer.
4. The backend evaluates the answer using:
   - DSA evaluator for algorithmic answers
   - heuristic evaluator for System Design answers
5. The backend can also run a simple RAG flow to retrieve concept notes and a multi-step agent workflow to generate structured responses.
6. The frontend renders the score and feedback.

### Backend Architecture

The backend is structured around clear layers:
- `controller`: handles HTTP and request/response boundaries
- `service`: owns orchestration and business logic
- `repository`: persists and fetches entities through Spring Data JPA
- `entity`: represents database models
- `dto`: defines API contracts
- `common`: contains shared API response and exception handling
- `config`: environment, CORS, seed data, and framework wiring
- `rag`: concept-note retrieval pipeline
- `agent`: multi-step AI workflow orchestration
- `evaluation`: DSA and System Design evaluation engines
- `dsa`: internal data-structure-driven utilities for showcase and evaluation

### Frontend Architecture

The frontend is intentionally simple and product-oriented:
- `pages`: route-level screens such as question list, practice, submission, and feedback
- `components`: shared UI blocks such as question cards and feedback panels
- `api`: backend integration through a typed API client
- `types`: shared TypeScript models for API responses

This structure keeps state close to where it is used and avoids introducing global state too early.

## Core Modules

### 1. Question Engine

The question bank stores:
- title
- type (`DSA` / `SYSTEM_DESIGN`)
- difficulty
- expected concepts
- sample answer

Why it matters:
- `expectedConcepts` acts as a lightweight rubric system
- `sampleAnswer` supports internal evaluation and future mentor tooling
- the same question model powers both practice and evaluation

### 2. RAG Layer

The RAG implementation stores concept notes in the database and retrieves the top-k notes for a user query.

Current simple version:
- store concept notes for DSA patterns and System Design topics
- create placeholder embeddings from token-frequency vectors
- rank notes with cosine similarity

Why retrieval matters:
- it grounds generated responses in platform-owned knowledge
- it reduces hallucination compared to a single free-form LLM call
- it creates a clean boundary between retrieval and generation

### 3. Agentic Workflow

The multi-step orchestration pipeline performs:
1. classify input as DSA or System Design
2. retrieve relevant concept notes with RAG
3. generate a structured response with:
   - explanation
   - approach
   - code for DSA
   - edge cases
   - follow-up questions

Why multi-step prompting is better than a single prompt:
- classification reduces ambiguity
- retrieval grounds the answer
- structured generation produces predictable output for the frontend

### 4. Evaluation Engine

#### DSA Evaluation

The DSA evaluator scores answers without executing code by checking:
- matched concepts
- missing concepts
- detected patterns
- complexity statements

This mirrors how interviewers often assess thinking before perfect implementation.

#### System Design Evaluation

The System Design evaluator uses heuristics to check:
- expected components like cache, database, queue, availability
- missing pieces in the architecture
- scalability concerns
- bottlenecks
- trade-off discussion

This is heuristic-based because System Design is open-ended and rarely has a single exact solution.

## Algorithms and Data Structures Used Internally

One strong part of this project is that DSA is not just the content domain. It is also used inside the implementation.

### Trie

Used to store and detect DSA patterns such as:
- sliding window
- dynamic programming
- graph
- BFS / DFS
- hash map
- two pointers

Why it is used:
- efficient pattern lookup over a known vocabulary
- fits phrase detection over learner answers
- demonstrates practical use of prefix-based structures

Complexity:
- insert: `O(m)` where `m` is pattern length
- lookup over text: depends on scan length and prefix overlap
- space: `O(total stored characters)`

### HashSet

Used in the DSA evaluator to compare user answers against expected concepts.

Why it is used:
- fast membership checks
- useful for normalized token matching
- simple and interview-relevant

Complexity:
- average lookup: `O(1)`
- space: `O(n)` for stored tokens

### HashMap

Used in the scoring engine to maintain weighted scoring buckets such as:
- concept coverage
- pattern recognition
- complexity presence
- penalties

Why it is used:
- flexible category-based scoring
- easy to extend with new dimensions
- maps well to scoring-card design

Complexity:
- average insert/get: `O(1)`
- space: `O(k)` for scoring categories

### Heap / Priority Queue

Used to rank feedback by importance.

Why it is used:
- the most important feedback should appear first
- missing concepts and bottlenecks matter more than low-priority notes
- this models prioritized mentoring instead of unordered strings

Complexity:
- insert: `O(log n)`
- remove max/highest priority: `O(log n)`
- peek: `O(1)`

## Redis Explanation

Redis is part of the intended production architecture even though the current scaffold does not yet wire it fully.

Where Redis fits naturally in AlgoMentor AI:
- caching frequently requested questions and concept notes
- caching RAG retrieval results for repeated queries
- rate limiting AI-heavy endpoints
- temporary session/state storage for multi-step workflows
- queue-backed background jobs in more advanced evaluation pipelines

Why Redis matters:
- reduces repeated database hits
- improves response time for repeated content access
- helps absorb traffic spikes
- creates a path toward asynchronous and event-driven workflows

A realistic future use would be:
- cache top DSA questions by difficulty
- cache concept-note retrieval for common topics like `hash map` or `load balancer`
- throttle agent workflow calls per user to control AI cost

## Interview Talking Points

You can use this project to discuss multiple backend and full-stack themes.

### Architecture Talking Points
- designed a layered Spring Boot backend with clear separation of HTTP, business logic, persistence, and DTO boundaries
- built a React + TypeScript frontend with route-driven product flows and typed API integration
- used Render-ready environment-based deployment design with a Dockerized backend and static frontend

### AI / Retrieval Talking Points
- implemented a simple RAG pipeline with database-backed concept notes, placeholder embeddings, and cosine-similarity retrieval
- built an agentic workflow that classifies input, retrieves knowledge, and returns a structured mentoring response
- separated retrieval from generation so the system remains easier to test and evolve

### Evaluation Talking Points
- built a DSA answer evaluator that scores reasoning without executing code by comparing rubric concepts and complexity statements
- added a heuristic System Design evaluator for expected components, scalability, bottlenecks, and trade-offs
- modeled evaluation like real interviews: pattern recognition first, then correctness signals, then feedback prioritization

### DSA Talking Points
- used a Trie for pattern detection inside learner answers
- used `HashSet` and `HashMap` in the scoring/evaluation engine
- used a `PriorityQueue` to rank feedback by severity
- made DSA part of the system implementation, not just the subject matter

### Product Thinking Talking Points
- designed the app as a real practice platform, not just an API demo
- kept the backend scalable by isolating retrieval, evaluation, and orchestration into separate services
- optimized for clarity and extensibility instead of collapsing everything into one AI endpoint

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- H2 for local development
- PostgreSQL for production

### Frontend
- React
- TypeScript
- React Router
- Vite

### AI / Evaluation
- RAG-style concept retrieval
- heuristic evaluation engines
- agentic orchestration flow

### Deployment
- Docker
- Render

## How To Run Locally

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 18+
- npm 9+

### 1. Run the Backend

From the project root:

```bash
cd backend
mvn spring-boot:run
```

The backend runs on:
- `http://localhost:8080`

Health check:
- `http://localhost:8080/api/v1/health`

### 2. Run the Frontend

In a new terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on:
- `http://localhost:3000`

By default, the frontend calls:
- `http://localhost:8080/api/v1`

### 3. Local Environment Variables

Use the root example file as a reference:
- [`.env.example`](./.env.example)

For frontend overrides, you can create `frontend/.env` with:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## How To Deploy

Deployment is documented in:
- [`DEPLOYMENT.md`](./DEPLOYMENT.md)
- [`render.yaml`](./render.yaml)

Production deployment model:
- frontend as a Render Static Site
- backend as a Render Docker Web Service
- PostgreSQL as a Render managed database

## Current Limitations

This project intentionally uses simple but extensible versions of several systems.

Current trade-offs:
- embeddings are placeholder vectors, not real embedding-model outputs
- question detail retrieval is still minimal
- DSA evaluation is reasoning-based, not execution-based
- System Design evaluation is heuristic, not semantically complete
- Redis is planned architecturally but not fully integrated yet
- production migrations, observability, and AI provider integration still need hardening

These are good trade-offs for an interview-grade project because they show architectural thinking, not just surface-level completeness.

## What Makes This Resume-Ready

This project demonstrates:
- full-stack engineering across backend, frontend, database, and deployment
- AI system design beyond a single LLM call
- practical use of data structures inside product logic
- evaluation and rubric design inspired by real interviews
- the ability to structure code like a production application

A strong one-line resume description would be:

> Built AlgoMentor AI, a full-stack DSA and System Design practice platform using Spring Boot, React, RAG-style retrieval, agentic workflows, heuristic answer evaluation, and Render-based deployment.

## Next Improvements

Good next steps if you continue the project:
- integrate a real embedding API and vector store
- add Redis-backed caching and rate limiting
- add Flyway or Liquibase migrations
- expose richer evaluation details to the frontend
- integrate a real LLM provider for grounded response generation
- add authentication and user progress dashboards
- add CI/CD and automated verification
