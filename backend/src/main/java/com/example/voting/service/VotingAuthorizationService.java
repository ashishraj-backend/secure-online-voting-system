package com.example.voting.service;

import com.example.voting.entity.Election;
import com.example.voting.entity.User;
import com.example.voting.entity.VotingAuthorization;
import com.example.voting.repository.VotingAuthorizationRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class VotingAuthorizationService {
    private final VotingAuthorizationRepository repository;
    private final StringRedisTemplate redis;

    public VotingAuthorizationService(VotingAuthorizationRepository repository, StringRedisTemplate redis) {
        this.repository = repository;
        this.redis = redis;
    }

    public String createAuthorization(User voter, Election election) {
        String token = UUID.randomUUID().toString();
        VotingAuthorization va = new VotingAuthorization();
        va.setVoter(voter);
        va.setElection(election);
        va.setToken(token);
        va.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
        repository.save(va);
        // store in redis short-lived index for quick lookup
        redis.opsForValue().set("va:" + token, voter.getId().toString(), 10 * 60);
        return token;
    }

    public boolean validateAndConsume(String token, User voter, Election election) {
        var maybe = repository.findByToken(token);
        if (maybe.isEmpty()) return false;
        VotingAuthorization va = maybe.get();
        if (va.isUsed() || va.getExpiresAt().isBefore(Instant.now())) return false;
        if (!va.getVoter().getId().equals(voter.getId())) return false;
        if (!va.getElection().getId().equals(election.getId())) return false;
        va.setUsed(true);
        repository.save(va);
        redis.delete("va:" + token);
        return true;
    }
}
