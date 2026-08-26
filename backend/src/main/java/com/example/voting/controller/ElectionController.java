package com.example.voting.controller;

import com.example.voting.entity.Candidate;
import com.example.voting.entity.Election;
import com.example.voting.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/elections")
public class ElectionController {
    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {this.electionService = electionService;}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Election e) {
        Election created = electionService.create(e);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/candidates")
    public ResponseEntity<?> addCandidate(@PathVariable UUID id, @RequestBody Candidate c) {
        Candidate created = electionService.addCandidate(id, c);
        return ResponseEntity.ok(created);
    }
}
