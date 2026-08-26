package com.example.voting.service;

import com.example.voting.entity.AuditLog;
import com.example.voting.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {this.repo = repo;}

    public void log(String eventType, String actorId, String details) {
        repo.save(new AuditLog(eventType, actorId, details));
    }
}
