# Secure Real-Time Online Voting System

This repository contains a production-approachable prototype for a secure real-time online voting system. It includes a Spring Boot backend, Flyway migrations for MySQL, Redis integration for caching and rate-limiting, STOMP over WebSocket for live results, and a React frontend.

Core components
- Backend: Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA
- Database migrations: Flyway (MySQL 8)
- Cache/short-lived tokens & rate-limiting: Redis
- Frontend: React 18 + Parcel
- Realtime: Spring STOMP over WebSocket
- CI: GitHub Actions (build & tests)

Quick start (recommended)

1. Copy `.env.example` to `.env` and set required variables: `JWT_SECRET`, `MYSQL_ROOT_PASSWORD`, `MYSQL_PASSWORD`.
2. Start the stack with Docker Compose:

```bash
docker compose up --build
```

This launches MySQL, Redis, the backend, and a production build of the frontend.

Developer flow (without Docker)

Backend (requires Java 21 + Maven):

```bash
cd backend
mvn -B spring-boot:run
```

Frontend (development):

```bash
cd frontend
npm install
npm run dev
```

Set `REACT_APP_API_BASE` to the backend base URL when running the frontend (default `http://localhost:8080`).

API highlights
- `POST /api/auth/register` — register a voter
- `POST /api/auth/login` — obtain JWT
- `GET /api/elections` — list elections
- `POST /api/elections/{id}/authorization` — request one-time voting authorization (requires JWT)
- `POST /api/elections/{id}/votes` — cast vote (requires JWT + `Authorization-Token` header)
- `GET /api/elections/{id}/results` — fetch current results
- WebSocket STOMP topic: `/topic/election.{id}.results` — subscribe to live tallies

Seeded admin (for development)
- email: `admin@example.com`
- password: `AdminPass123`

Security & safety notes
- Passwords hashed with BCrypt.
- JWT bearer authentication used for both REST and STOMP CONNECT.
- Database-level unique constraint prevents duplicate votes; service layer marks one-time voting authorizations as consumed.
- Redis used for one-time tokens and rate-limiting to mitigate automated abuse.

Running tests

Backend tests (unit & integration):

```bash
cd backend
mvn test
```

CI / GitHub Actions
- The repo contains workflows under `.github/workflows/` that build and test the backend and build the frontend. Pushing to `main` triggers CI.

Where to look
- Backend main: `backend/src/main/java`
- Flyway migrations: `backend/src/main/resources/db/migration`
- Frontend: `frontend/src`
- Compose orchestration: `docker/docker-compose.yml`

Next steps I will perform (sequential)
1. Finish and expand README with diagrams and API examples (this file).
2. Attempt to install Maven locally (in workspace) and build the backend artifact.
3. Install Docker Desktop and start the stack with `docker compose up --build` (requires approving installer GUI on Windows).
4. Run smoke tests against the running backend and frontend; then finalize documentation and CI notes.

If you want me to start step 2 now (install Maven and build), reply `start step 2`. If you prefer I proceed fully and handle prompts, reply `go` and I'll continue.



