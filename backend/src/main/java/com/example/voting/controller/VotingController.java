package com.example.voting.controller;

import com.example.voting.entity.Election;
import com.example.voting.entity.Vote;
import com.example.voting.entity.User;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.repository.UserRepository;
import com.example.voting.service.VotingAuthorizationService;
import com.example.voting.service.VotingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/elections")
public class VotingController {
    private final VotingAuthorizationService authService;
    private final VotingService votingService;
    private final UserRepository userRepository;
    private final ElectionRepository electionRepository;
    private final com.example.voting.service.ElectionService electionService;
    private final com.example.voting.service.RateLimitService rateLimitService;

    public VotingController(VotingAuthorizationService authService, VotingService votingService, UserRepository userRepository, ElectionRepository electionRepository, com.example.voting.service.ElectionService electionService, com.example.voting.service.RateLimitService rateLimitService) {
        this.authService = authService;
        this.votingService = votingService;
        this.userRepository = userRepository;
        this.electionRepository = electionRepository;
        this.electionService = electionService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/{id}/authorization")
    public ResponseEntity<?> requestAuthorization(@PathVariable UUID id, Authentication auth) {
        User user = userRepository.findById(UUID.fromString((String)auth.getPrincipal())).orElseThrow();
        Election e = electionService.getById(id);
        String key = "rl:auth:" + user.getId() + ":" + id;
        if (!rateLimitService.incrementAndCheck(key, 5, java.time.Duration.ofMinutes(1))) throw new com.example.voting.exception.RateLimitExceededException("Too many authorization requests");
        String token = authService.createAuthorization(user, e);
        return ResponseEntity.ok(java.util.Map.of("token", token));
    }

    @PostMapping("/{id}/votes")
    public ResponseEntity<?> castVote(@PathVariable UUID id, @RequestBody Vote vote, @RequestHeader("Authorization-Token") String token, Authentication auth) {
        User user = userRepository.findById(UUID.fromString((String)auth.getPrincipal())).orElseThrow();
        Election e = electionService.getById(id);
        String key = "rl:vote:" + user.getId() + ":" + id;
        if (!rateLimitService.incrementAndCheck(key, 3, java.time.Duration.ofMinutes(1))) throw new com.example.voting.exception.RateLimitExceededException("Too many vote attempts");
        if (!authService.validateAndConsume(token, user, e)) return ResponseEntity.status(403).body(java.util.Map.of("error","InvalidAuthorization"));
        vote.setVoter(user);
        vote.setElection(e);
        try {
            var saved = votingService.castVote(user, vote, token);
            return ResponseEntity.ok(java.util.Map.of("voteId", saved.getId()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
