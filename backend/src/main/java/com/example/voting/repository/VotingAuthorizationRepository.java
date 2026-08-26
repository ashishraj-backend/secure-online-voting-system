package com.example.voting.repository;

import com.example.voting.entity.VotingAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VotingAuthorizationRepository extends JpaRepository<VotingAuthorization, UUID> {
    Optional<VotingAuthorization> findByToken(String token);
}
