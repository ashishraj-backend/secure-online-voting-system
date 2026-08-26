package com.example.voting.repository;

import com.example.voting.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    List<Candidate> findByElectionId(UUID electionId);
}
