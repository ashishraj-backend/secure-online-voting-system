package com.example.voting.controller;

import com.example.voting.entity.AuditLog;
import com.example.voting.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AuditController {
    private final AuditLogRepository repo;

    public AuditController(AuditLogRepository repo) {this.repo = repo;}

    @GetMapping("/audit-logs")
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        var p = repo.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(p);
    }
}
