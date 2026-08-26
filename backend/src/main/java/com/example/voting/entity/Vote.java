package com.example.voting.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "votes", uniqueConstraints = {@UniqueConstraint(columnNames = {"voter_id","election_id"}, name = "uk_voter_election")})
public class Vote {
    @Id
    @GeneratedValue
    private java.util.UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voter_id")
    private User voter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "election_id")
    private Election election;

    @ManyToOne(optional = false)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    private Instant castAt = Instant.now();

    public java.util.UUID getId() {return id;}
    public User getVoter() {return voter;}
    public void setVoter(User voter) {this.voter = voter;}
    public Election getElection() {return election;}
    public void setElection(Election election) {this.election = election;}
    public Candidate getCandidate() {return candidate;}
    public void setCandidate(Candidate candidate) {this.candidate = candidate;}
}
