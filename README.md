# Secure Online Voting System

This repository contains a full-stack secure online voting prototype: Java Spring Boot backend, MySQL, Redis, WebSocket/STOMP real-time results, and a React frontend.

Quick start (recommended: Docker Compose)

1. Create a `.env` or set environment variables: `JWT_SECRET`, `MYSQL_ROOT_PASSWORD`, `MYSQL_PASSWORD`.

2. Start with Docker Compose:

```bash
docker compose up --build
```

3. Or run services locally:

Backend:
```bash
cd backend
mvn -B spring-boot:run
```

Frontend:
```bash
cd frontend
npm install
# dev
npm run dev
# or build
npm run build
```

Set frontend to talk to backend via `REACT_APP_API_BASE` env var (e.g. `http://localhost:8080`).

Admin seeded account (if seeding enabled): `admin@example.com` / `AdminPass123`.

Pushing to GitHub
- Create a repository on GitHub and add it as a remote, or provide a Personal Access Token for automated push steps.

See `docker/docker-compose.yml` and `backend/src/main/resources/db/migration` for DB schema and service config.# Secure Real-Time Online Voting System

This repository contains a Spring Boot backend and a simple React frontend for a secure real-time online voting system prototype.

Backend: `backend/` — Spring Boot (Java 21, Maven)
Frontend: `frontend/` — React + Parcel

Run locally via Docker Compose:

```bash
docker compose up --build
```

## Architecture

- Backend: Spring Boot 3.x application (`backend/`) exposing REST APIs and WebSocket endpoints.
- Database: MySQL (migrations in `backend/src/main/resources/db/migration`).
- Cache/Temporary data: Redis for rate limiting and voting authorizations.
- Frontend: React (minimal demo) in `frontend/` connecting via REST and WebSocket.

## API Endpoints (high level)

- `POST /api/auth/register` — Register voter
- `POST /api/auth/login` — Login
- `GET /api/elections` — List elections
- `POST /api/admin/elections` — Admin create election
- `POST /api/elections/{id}/authorization` — Request one-time voting authorization
- `POST /api/elections/{id}/votes` — Cast vote with `Authorization-Token` header
- `GET /api/elections/{id}/results` — Get results

See OpenAPI UI at `/swagger-ui.html` when running the app.

## Security Notes

- Set `JWT_SECRET` and other credentials via environment variables (see `.env.example`).
- Do not run with embedded H2 in production.

## Tests

Run backend tests:

```bash
cd backend
mvn test
```


