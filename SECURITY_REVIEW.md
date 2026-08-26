# Security Review Notes

Summary of checks performed and items to verify before production:

- No plaintext passwords stored: BCrypt is used (`PasswordEncoder`).
- JWT secrets must be set via environment variable `JWT_SECRET` (do not commit secrets).
- Unique DB constraint on `votes(voter_id,election_id)` prevents duplicates at DB-level.
- Voting uses transactional `castVote` and catches `DataIntegrityViolationException` to handle duplicates.
- Redis is used for rate limiting and voting authorization tokens; ensure Redis is access-controlled in deployment.
- Validate CORS in production (current config allows all origins for WebSocket).
- Do not use `spring.jpa.hibernate.ddl-auto=create` in production; migrations via Flyway are used.
- Audit logs are recorded in `audit_logs` table; sensitive data is not stored.

Remaining risks and recommendations:
- WebSocket authentication currently relies on HTTP session / tokens; consider adding STOMP header token validation.
- Ensure HTTPS termination at load balancer; do not expose services over plaintext HTTP in production.
- Rotate JWT secrets and use sufficiently long random keys.
- Add monitoring/alerting for Redis and DB access patterns.
