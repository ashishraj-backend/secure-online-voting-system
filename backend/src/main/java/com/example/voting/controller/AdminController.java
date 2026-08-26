package com.example.voting.controller;

import com.example.voting.entity.Candidate;
import com.example.voting.entity.Election;
import com.example.voting.service.AuditService;
import com.example.voting.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ElectionService electionService;
    private final AuditService auditService;
    private final com.example.voting.repository.ElectionRepository electionRepository;

    public AdminController(ElectionService electionService, AuditService auditService, com.example.voting.repository.ElectionRepository electionRepository) {
        this.electionService = electionService;
        this.auditService = auditService;
        this.electionRepository = electionRepository;
    }

    @PostMapping("/elections")
    public ResponseEntity<?> create(@RequestBody Election e) {
        Election saved = electionService.create(e);
        auditService.log("ELECTION_CREATED", null, "Election " + saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/elections/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Election e) {
        Election existing = electionRepository.findById(id).orElseThrow(() -> new com.example.voting.exception.ElectionNotFoundException("Not found"));
        existing.setTitle(e.getTitle());
        existing.setDescription(e.getDescription());
        existing.setStartTime(e.getStartTime());
        existing.setEndTime(e.getEndTime());
        existing.setStatus(e.getStatus());
        Election saved = electionRepository.save(existing);
        electionService.evictCache(id);
        auditService.log("ELECTION_UPDATED", null, "Election " + id);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/elections/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        electionRepository.deleteById(id);
        electionService.evictCache(id);
        auditService.log("ELECTION_DELETED", null, "Election " + id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/elections/{id}/schedule")
    public ResponseEntity<?> schedule(@PathVariable UUID id, @RequestParam Instant start, @RequestParam Instant end) {
        Election e = electionService.scheduleElection(id, start, end);
        auditService.log("ELECTION_SCHEDULED", null, "Election " + id);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/elections/{id}/start")
    public ResponseEntity<?> start(@PathVariable UUID id) {
        Election e = electionService.startElection(id);
        auditService.log("ELECTION_STARTED", null, "Election " + id);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/elections/{id}/close")
    public ResponseEntity<?> close(@PathVariable UUID id) {
        Election e = electionService.closeElection(id);
        auditService.log("ELECTION_CLOSED", null, "Election " + id);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/elections/{id}/publish")
    public ResponseEntity<?> publish(@PathVariable UUID id) {
        Election e = electionService.publishResults(id);
        auditService.log("RESULTS_PUBLISHED", null, "Election " + id);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/elections/{id}/candidates")
    public ResponseEntity<?> addCandidate(@PathVariable UUID id, @RequestBody Candidate c) {
        var saved = electionService.addCandidate(id, c);
        auditService.log("CANDIDATE_ADDED", null, "Candidate " + saved.getId() + " to election " + id);
        return ResponseEntity.ok(saved);
    }
}
