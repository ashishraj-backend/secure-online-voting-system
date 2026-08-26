-- Flyway baseline
CREATE TABLE roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
  id BINARY(16) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
  user_id BINARY(16) NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
);

CREATE TABLE elections (
  id BINARY(16) PRIMARY KEY,
  title VARCHAR(500) NOT NULL,
  description TEXT,
  start_time TIMESTAMP NULL,
  end_time TIMESTAMP NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE candidates (
  id BINARY(16) PRIMARY KEY,
  election_id BINARY(16) NOT NULL,
  name VARCHAR(255) NOT NULL,
  FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE
);

CREATE TABLE votes (
  id BINARY(16) PRIMARY KEY,
  voter_id BINARY(16) NOT NULL,
  election_id BINARY(16) NOT NULL,
  candidate_id BINARY(16) NOT NULL,
  cast_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_voter_election (voter_id, election_id),
  FOREIGN KEY (voter_id) REFERENCES users(id),
  FOREIGN KEY (election_id) REFERENCES elections(id),
  FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

CREATE TABLE voting_authorizations (
  id BINARY(16) PRIMARY KEY,
  voter_id BINARY(16) NOT NULL,
  election_id BINARY(16) NOT NULL,
  token VARCHAR(255) NOT NULL UNIQUE,
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (voter_id) REFERENCES users(id),
  FOREIGN KEY (election_id) REFERENCES elections(id)
);

CREATE TABLE audit_logs (
  id BINARY(16) PRIMARY KEY,
  event_type VARCHAR(255) NOT NULL,
  actor_id VARCHAR(255),
  details TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
