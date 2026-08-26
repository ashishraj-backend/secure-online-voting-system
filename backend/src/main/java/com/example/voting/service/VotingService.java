package com.example.voting.service;

import com.example.voting.entity.Vote;
import com.example.voting.entity.VotingAuthorization;
import com.example.voting.entity.User;
import com.example.voting.repository.VoteRepository;
import com.example.voting.repository.VotingAuthorizationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.example.voting.repository.CandidateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VotingService {
    private final VoteRepository voteRepository;
    private final VotingAuthorizationRepository authRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CandidateRepository candidateRepository;

    public VotingService(VoteRepository voteRepository, VotingAuthorizationRepository authRepository, SimpMessagingTemplate messagingTemplate, CandidateRepository candidateRepository) {
        this.voteRepository = voteRepository;
        this.authRepository = authRepository;
        this.messagingTemplate = messagingTemplate;
        this.candidateRepository = candidateRepository;
    }

    @Transactional
    public Vote castVote(User voter, Vote vote, String authToken) {
        VotingAuthorization va = authRepository.findByToken(authToken).orElseThrow(() -> new com.example.voting.exception.InvalidVotingAuthorizationException("InvalidVotingAuthorization"));
        if (va.isUsed() || va.getExpiresAt().isBefore(Instant.now())) throw new com.example.voting.exception.InvalidVotingAuthorizationException("InvalidVotingAuthorization");
        if (!va.getVoter().getId().equals(voter.getId()) || !va.getElection().getId().equals(vote.getElection().getId())) throw new com.example.voting.exception.InvalidVotingAuthorizationException("UnauthorizedVoting");
        try {
            // attempt to save vote; unique constraint prevents duplicates
            Vote saved = voteRepository.save(vote);
            va.setUsed(true);
            authRepository.save(va);
            publishResults(vote.getElection().getId());
            return saved;
        } catch (DataIntegrityViolationException ex) {
            throw new com.example.voting.exception.AlreadyVotedException("Duplicate vote detected");
        }
    }

    private void publishResults(UUID electionId) {
        long total = voteRepository.countByElectionId(electionId);
        var candidates = candidateRepository.findByElectionId(electionId);
        var results = candidates.stream().map(c -> {
            long votes = voteRepository.countByElectionIdAndCandidateId(electionId, c.getId());
            double pct = total == 0 ? 0 : (votes * 100.0 / total);
            return new com.example.voting.dto.ResultDto(c.getId().toString(), c.getName(), votes, pct);
        }).collect(Collectors.toList());
        messagingTemplate.convertAndSend("/topic/election." + electionId + ".results", results);
    }
}
