package com.example.voting.repository;

import com.example.voting.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    Optional<Vote> findByVoterIdAndElectionId(UUID voterId, UUID electionId);
    long countByElectionId(UUID electionId);
    long countByElectionIdAndCandidateId(UUID electionId, UUID candidateId);
}
