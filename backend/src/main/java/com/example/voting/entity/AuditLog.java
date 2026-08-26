package com.example.voting.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = {@Index(columnList = "eventType", name = "idx_audit_event")})
public class AuditLog {
    @Id
    @GeneratedValue
    private java.util.UUID id;

    private String eventType;

    private String actorId;

    @Column(length = 2000)
    private String details;

    private Instant createdAt = Instant.now();

    public AuditLog() {}
    public AuditLog(String eventType, String actorId, String details) {
        this.eventType = eventType; this.actorId = actorId; this.details = details;
    }

    public java.util.UUID getId() {return id;}
    public String getEventType() {return eventType;}
    public String getActorId() {return actorId;}
    public String getDetails() {return details;}
    public Instant getCreatedAt() {return createdAt;}
}
