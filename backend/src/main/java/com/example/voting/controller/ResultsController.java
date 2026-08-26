package com.example.voting.controller;

import com.example.voting.dto.ResultDto;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elections")
public class ResultsController {
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;

    public ResultsController(CandidateRepository candidateRepository, VoteRepository voteRepository) {
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> getResults(@PathVariable UUID id) {
        long total = voteRepository.countByElectionId(id);
        var candidates = candidateRepository.findByElectionId(id);
        List<ResultDto> results = candidates.stream().map(c -> {
            long votes = voteRepository.countByElectionIdAndCandidateId(id, c.getId());
            double pct = total == 0 ? 0 : (votes * 100.0 / total);
            return new ResultDto(c.getId().toString(), c.getName(), votes, pct);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(java.util.Map.of("totalVotes", total, "results", results));
    }
}
