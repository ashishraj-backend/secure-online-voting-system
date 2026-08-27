# Secure Real-Time Online Voting System

A prototype online voting application with a Spring Boot API and React frontend. The backend uses MySQL for persistence, Flyway for schema migrations, Redis for caching and short-lived voting authorizations, and STOMP over WebSocket for live result updates.

## Features

- Voter registration and JWT-based login
- Admin election and candidate management
- Scheduled election lifecycle: schedule, start, close, and publish results
- One-time voting authorization tokens
- Duplicate-vote protection at the service and database layers
- Rate limiting for registration, login, authorization, and voting attempts
- Live election results over WebSocket
- Health and OpenAPI endpoints

## Technology

- Backend: Java 21, Spring Boot 3.2, Spring Security, Spring Data JPA
- Database: MySQL 8 with Flyway migrations
- Cache and rate limiting: Redis 7
- Frontend: React 18, React Router, Parcel
- Local orchestration: Docker Compose

## Quick Start With Docker

Prerequisites: Docker Desktop with Compose enabled.

From the repository root, run:

```bash
docker compose -f docker/docker-compose.yml up --build
```

The services are available at:

| Service | URL | Purpose |
| --- | --- | --- |
| Frontend | http://localhost:3001 | React application |
| Backend API | http://localhost:8081 | REST API |
| Backend health | http://localhost:8081/actuator/health | Service health |
| Swagger UI | http://localhost:8081/swagger-ui/index.html | API documentation |
| MySQL | `localhost:3307` | Database access |
| Redis | `localhost:6380` | Cache access |

The Compose file currently uses development credentials. Change them before using the application outside a local environment, especially `JWT_SECRET`, database passwords, and the seeded admin password.

Stop the services with:

```bash
docker compose -f docker/docker-compose.yml down
```

To remove the persisted MySQL volume as well:

```bash
docker compose -f docker/docker-compose.yml down -v
```

## Run Without Docker

Start MySQL and Redis separately, then configure the backend with environment variables. Java 21 and Maven are required.

```bash
cd backend
mvn spring-boot:run
```

The backend defaults to `http://localhost:8080` and uses these local defaults:

```text
JDBC_URL=jdbc:mysql://localhost:3307/voting?useSSL=false&allowPublicKeyRetrieval=true
DB_USER=user
DB_PASSWORD=password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=change_this_in_env
```

For the frontend, install Node.js 18 or newer and run:

```bash
cd frontend
npm install
npm start
```

Parcel serves the frontend at `http://localhost:3000`. The frontend API base URL is configured in `frontend/src/services/api.js`; use the backend URL that matches how the backend was started.

## API Overview

Authentication endpoints return a JWT in a response such as `{ "token": "..." }`.

```http
POST /api/auth/register
Content-Type: application/json

{
	"name": "Example Voter",
	"email": "voter@example.com",
	"password": "StrongPassword123"
}
```

```http
POST /api/auth/login
Content-Type: application/json

{
	"email": "voter@example.com",
	"password": "StrongPassword123"
}
```

Voting requires both the JWT bearer token and a fresh authorization token:

```http
POST /api/elections/{electionId}/authorization
Authorization: Bearer <jwt>
```

```http
POST /api/elections/{electionId}/votes
Authorization: Bearer <jwt>
Authorization-Token: <one-time-token>
Content-Type: application/json
```

Additional endpoints include:

- `GET /api/elections/{id}/results` to retrieve totals and candidate percentages
- `POST /api/admin/elections` to create an election
- `PUT /api/admin/elections/{id}` to update an election
- `DELETE /api/admin/elections/{id}` to delete an election
- `POST /api/admin/elections/{id}/candidates` to add a candidate
- `POST /api/admin/elections/{id}/schedule?start=<ISO-8601>&end=<ISO-8601>` to schedule an election
- `POST /api/admin/elections/{id}/start`, `/close`, and `/publish` for lifecycle transitions
- `GET /api/admin/audit-logs` to read audit events

The WebSocket endpoint is `/ws`. STOMP clients subscribe to `/topic/election.{id}.results` for result updates. Admin endpoints require an authenticated admin account.

## Development Admin

The development seed data provides:

```text
Email:    admin@example.com
Password: AdminPass123
```

Do not use these credentials in a deployed environment.

## Testing

Run backend tests with:

```bash
cd backend
mvn test
```

Build the frontend with:

```bash
cd frontend
npm run build
```

## Security Notes

- Passwords are hashed with BCrypt.
- JWTs protect REST requests and WebSocket STOMP connections.
- Authorization tokens are short-lived and consumed after use.
- Redis-backed rate limits reduce automated abuse of sensitive endpoints.
- Database constraints and service checks prevent duplicate votes.
- This is a prototype and requires a security review, production secret management, HTTPS, monitoring, and an appropriate election audit process before real-world use.

## Repository Layout

```text
backend/                  Spring Boot API and tests
backend/src/main/resources/db/migration/
													Flyway database migrations
frontend/                 React application
docker/docker-compose.yml Local MySQL, Redis, backend, and frontend stack
```

There is currently no GitHub Actions workflow in this repository. CI can be added under `.github/workflows/` when the project is ready for automated builds and tests.



