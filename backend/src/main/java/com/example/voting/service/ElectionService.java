package com.example.voting.service;

import com.example.voting.entity.Candidate;
import com.example.voting.entity.Election;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.UUID;

@Service
public class ElectionService {
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    public ElectionService(ElectionRepository electionRepository, CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
    }

    @Transactional
    public Election create(Election e) {
        var saved = electionRepository.save(e);
        return saved;
    }

    @Cacheable(value = "elections", key = "#id")
    public Election getById(UUID id) {
        return electionRepository.findById(id).orElseThrow();
    }

    @CacheEvict(value = "elections", key = "#id")
    public void evictCache(UUID id) {
        // noop used to evict cache after updates
    }

    @Transactional
    public Candidate addCandidate(UUID electionId, Candidate candidate) {
        Election e = electionRepository.findById(electionId).orElseThrow();
        candidate.setElection(e);
        return candidateRepository.save(candidate);
    }

    @Transactional
    public Election scheduleElection(UUID electionId, java.time.Instant start, java.time.Instant end) {
        Election e = electionRepository.findById(electionId).orElseThrow(() -> new com.example.voting.exception.ElectionNotFoundException("Election not found"));
        if (e.getStatus() != Election.Status.DRAFT && e.getStatus() != Election.Status.SCHEDULED) throw new com.example.voting.exception.InvalidElectionStateException("Can only schedule from DRAFT or SCHEDULED");
        if (start == null || end == null || !end.isAfter(start)) throw new com.example.voting.exception.InvalidElectionStateException("Invalid start/end");
        e.setStartTime(start);
        e.setEndTime(end);
        e.setStatus(Election.Status.SCHEDULED);
        var saved = electionRepository.save(e);
        evictCache(electionId);
        return saved;
    }

    @Transactional
    public Election startElection(UUID electionId) {
        Election e = electionRepository.findById(electionId).orElseThrow(() -> new com.example.voting.exception.ElectionNotFoundException("Election not found"));
        if (e.getStatus() != Election.Status.SCHEDULED && e.getStatus() != Election.Status.DRAFT) throw new com.example.voting.exception.InvalidElectionStateException("Can only start from SCHEDULED or DRAFT");
        if (e.getStartTime() != null && e.getStartTime().isAfter(java.time.Instant.now())) throw new com.example.voting.exception.InvalidElectionStateException("Cannot start before startTime");
        e.setStatus(Election.Status.LIVE);
        var saved = electionRepository.save(e);
        evictCache(electionId);
        return saved;
    }

    @Transactional
    public Election closeElection(UUID electionId) {
        Election e = electionRepository.findById(electionId).orElseThrow(() -> new com.example.voting.exception.ElectionNotFoundException("Election not found"));
        if (e.getStatus() != Election.Status.LIVE) throw new com.example.voting.exception.InvalidElectionStateException("Can only close LIVE elections");
        e.setStatus(Election.Status.CLOSED);
        var saved = electionRepository.save(e);
        evictCache(electionId);
        return saved;
    }

    @Transactional
    public Election publishResults(UUID electionId) {
        Election e = electionRepository.findById(electionId).orElseThrow(() -> new com.example.voting.exception.ElectionNotFoundException("Election not found"));
        if (e.getStatus() != Election.Status.CLOSED) throw new com.example.voting.exception.InvalidElectionStateException("Can only publish from CLOSED");
        e.setStatus(Election.Status.RESULTS_PUBLISHED);
        var saved = electionRepository.save(e);
        evictCache(electionId);
        return saved;
    }
}
