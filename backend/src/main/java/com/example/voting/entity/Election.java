package com.example.voting.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "elections")
public class Election {
    @Id
    @GeneratedValue
    private java.util.UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private Instant startTime;
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidate> candidates = new ArrayList<>();

    private Instant createdAt = Instant.now();

    public enum Status {DRAFT, SCHEDULED, LIVE, CLOSED, RESULTS_PUBLISHED}

    // getters / setters
    public java.util.UUID getId() {return id;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public Instant getStartTime() {return startTime;}
    public void setStartTime(Instant startTime) {this.startTime = startTime;}
    public Instant getEndTime() {return endTime;}
    public void setEndTime(Instant endTime) {this.endTime = endTime;}
    public Status getStatus() {return status;}
    public void setStatus(Status status) {this.status = status;}
    public List<Candidate> getCandidates() {return candidates;}
}
